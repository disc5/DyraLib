# -*- coding: utf-8 -*-
"""
 PLNet for fixed sized input rankings.


 Version: M fixed but generic
 Idea: Working with M networks in parallel that permanently remain in memory.
 Testbed - label ranking / housing dataset
 
@author: dirk
"""
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import tensorflow as tf
import numpy as np
import dypy as dp
import dypy.tf_plnet_utils as plnetutils

#%%
import os
os.chdir('.')

#%%
X,R = dp.io.read_xxl_file_as_matrices('data/housing_dense_tr.txt')

#%%
Orderings = dp.utils.convert_rankingmat_to_orderingmat(R).tolist()
M = len(Orderings[0])
Y = np.eye(M, dtype=np.int32).tolist()

#%%
Z = dp.utils.create_contextualized_concat_orderings(X.tolist(), Y, Orderings)

#%%
input_dim = len(Z[0][0])

#%% 
tf.set_random_seed(1)

#%%
# Parameters
learn_rate = 0.01
n_epochs = 10

# Network parameters
num_input_neurons = input_dim
num_hidden_neurons = 5

# Network input
net_inputs = []
for i0 in range(M):
    net_inputs.append(tf.placeholder(tf.float32, shape = (num_input_neurons,1), name='input_net'+str(i0+1)))


#%% Helper functions
def sigma(x):
    return tf.div(tf.constant(1.0),
                  tf.add(tf.constant(1.0), tf.exp(tf.negative(x))))

def sigmaprime(x):
    return tf.multiply(sigma(x), tf.subtract(tf.constant(1.0), sigma(x)))

#%%
# Create graph procedure
def create_graph(inp, name="fc"):
    with tf.name_scope(name):
        weights_h1 = tf.Variable(tf.truncated_normal([num_input_neurons, num_hidden_neurons], seed=1), name="w1", trainable=True)
        biases_b1 =  tf.Variable(tf.truncated_normal([1, num_hidden_neurons], seed=2), name="bias1", trainable=True)       
        weights_out = tf.Variable(tf.truncated_normal([num_hidden_neurons, 1], seed=3), name="w2", trainable=True)  
        tf.summary.histogram("weights_h1", weights_h1)
        tf.summary.histogram("biases_b1", biases_b1)
        tf.summary.histogram("weights_out", weights_out)
        z1 = tf.add(tf.matmul(inp, weights_h1, transpose_a=True), biases_b1, name="z1_element")
        a1 = tf.sigmoid(z1, name='a1_element')
        z2 = tf.matmul(a1, weights_out)
        #z2 = tf.add(tf.matmul(a1, weights_out), biases_b2)
        u = tf.identity(z2, name="output") # this corresponds to utility u
        return (u, [a1,z1])
#%%
net_outputs = []
net_elements = []
for i0 in range(M):
    u, inner_parts = create_graph(net_inputs[i0], 'net'+str(i0+1))
    net_outputs.append(u)
    net_elements.append(inner_parts)        

#%% Network ranking losses
net_ranking_losses = []
for i0 in range(M):
    net_ranking_losses.append(plnetutils.tf_calculate_derivative((i0+1), net_outputs))

#%% Trainable network variables
trainable_network_vars = []
for i0 in range(M):
    trainable_network_vars.append(tf.get_collection(tf.GraphKeys.TRAINABLE_VARIABLES, scope='net'+str(i0+1)))



#%% get_gradients func
def get_gradients(delta, tvs, inner_parts, input_element, name):
    ''' Preliminary custom gradients function.
    It calculates gradients for a single-hidden layer feedforward neural 
    network SLFN only.
    It expects the tvs to be the sequence (w1, b1, w2).
    '''
    #g=tf.get_default_graph()
    #g.get_operations()[41].name
                    # todo filter operations to recieve a1 ...
    a1 = inner_parts[0]
    z1 = inner_parts[1]
    w2 = tvs[2]
    with tf.name_scope(name):
        d_w_2 = tf.transpose(tf.matmul(delta, a1), name="dw2")
        #d_b_2 = tf.constant(0.0)
        sp = sigmaprime(z1)
        delta2 = tf.multiply(tf.transpose(tf.multiply(w2,delta)),sp)
        d_b_1 = delta2
        d_w_1 = tf.transpose(tf.matmul(tf.transpose(delta2), tf.transpose(input_element)), name="dw1")
    
    return [d_w_1, d_b_1, d_w_2]
#%% Net gradients (v2: with grad_ys info)
#net_gradients_orig = []
#for i0 in range(M):
#    net_gradients_orig.append(tf.gradients(net_ranking_losses[i0], trainable_network_vars[i0], name = 'grad'+str(i0+1)))

#%%
net_gradients = []
for i0 in range(M):
    net_gradients.append(get_gradients(net_ranking_losses[i0], trainable_network_vars[i0], net_elements[i0], net_inputs[i0], 'net'+str(i0+1)))

#%% Accumulation operation v2
acc_ops = []
num_layers = len(net_gradients[0])
for i0 in range(num_layers):
    layer_list = []    
    for i1 in range(M):
        layer_list.append(net_gradients[i1][i0])
    acc_ops.append(tf.add_n(layer_list, name='acc_weights_layer'+str(i0+1)))
    
#%% Accumulation operation
#net_gradients_accumulation = net_gradients[0]
#for i1 in range(1,M):
#    net_gradients_accumulation = net_gradients_accumulation + net_gradients[i1]

#%% Optimization operations
opt = tf.train.GradientDescentOptimizer(learn_rate)
training_ops = []
for i0 in range(M):
    training_ops.append(opt.apply_gradients(zip(acc_ops, trainable_network_vars[i0])))

#%% Begin Session
sess = tf.Session()
init = tf.global_variables_initializer()
sess.run(init)

merged_summary = tf.summary.merge_all()
writer = tf.summary.FileWriter("/tmp/plnet_tf/2")
writer.add_graph(sess.graph)

#%% Training
N_tr = len(Z)
for epoch in range(n_epochs):
    RP = np.random.permutation(N_tr)
    for i1 in range(N_tr):
        ct_observation = Z[RP[i1]]   
        current_dict = {}
        for i0 in range(M):
            current_dict[net_inputs[i0].name] = np.asarray(ct_observation[i0]).reshape((input_dim,1))
        sess.run([net_gradients, training_ops], feed_dict=current_dict)               
    nll_data = plnetutils.get_NLL_dataset(sess, net_inputs[0], net_outputs[0], Z) 
    print("Epoch %i: NLL(tr) : %3.4f" %(epoch,nll_data))

#%% Close Session
sess.close()

