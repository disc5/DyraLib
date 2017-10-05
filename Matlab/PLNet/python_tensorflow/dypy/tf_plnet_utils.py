# -*- coding: utf-8 -*-
"""
Auxiliary Functions for TensorFlow PLNet
2017
@author: ds
"""

import tensorflow as tf
import numpy as np

#%% Auxiliary functions
def tf_calculate_derivative(rank_pos, observation_output_activations):
    """Returns the tf symbolic cost derivative for an item at a rank position.
    observation_output_activations refers to the output activation of 
    each observation item (probably acquired in parallel).
    
    Args:
        rank_pos : 1<= i <= M
        observation_output_activations: list of M PLNet outputs
    
    Returns:
        derivative for the i-th utility
        
    Note: this implementation assumes that that the output_activations
    of an observation are in the "correct" or desired ordering already."""
    cost = 0.0      
    M = len(observation_output_activations)  
    for k in range(0,M-1):
      if (rank_pos-1 >= k):
        denom = 0.0
        for l in range(k,M):
          denom = tf.add(denom, tf.exp(observation_output_activations[l])  ) 
        cost = tf.add(cost, tf.div(tf.exp(observation_output_activations[rank_pos-1]),denom))
    if (rank_pos < M):        
      cost = tf.subtract(cost,1.0)        
    return cost

def calculate_derivative(rank_pos, observation_output_activations):
    """Returns the cost derivative for an item at a rank position.
    observation_output_activations refers to the output activation of 
    each observation item (probably acquired in parallel).
    
    Args:
        rank_pos : 1<= i <= M
        observation_output_activations: list of M PLNet outputs
    
    Returns:
        derivative for the i-th utility
        
    Note: this implementation assumes that that the output_activations
    of an observation are in the "correct" or desired ordering already."""
    cost = 0.0      
    M = len(observation_output_activations)  
    for k in range(0,M-1):
      if (rank_pos-1 >= k):
        denom = 0.0
        for l in range(k,M):
          denom = denom + np.exp(observation_output_activations[l])   
        cost = cost + np.exp(observation_output_activations[rank_pos-1])/denom
    if (rank_pos < M):        
      cost = cost - 1.0        
    return cost

def getNLLValue(utilities):
    """Calculates the NLL for a given list of utility values.
    The NLL of the Plackett-Luce model is the objective we
    aim to minimize. For now we assume that the utilities are
    in the correct ordering.
    
    Args:
        utilities: an ordered vector of scalars.
        
    Returns:
        NLL - a scalar.
    
    Note:
        utilities correspond to the outputs of the network (1..M).
    """
    M = len(utilities)
    nll = 0     
    #M = 6#5   
    for k in range(0,M):
      logsum = 0;
      for l in range(k,M):
        logsum = logsum + np.exp(utilities[l])
      nll = nll + np.log(logsum)
    for k in range(0,M):
      nll = nll - utilities[k]
    return nll

def construct_symbolic_utility_vector(rank_position, output_variable, utilities_vector):
    ''' Includes a TF variable into the vector of utilities. 
    
        Args:
            rank_position - int between 1 (the top rank) and M.
            output_variable - symbolic variable that is placed at rank_position in the vector.
            utilities_vector - a vector of M reals

        Returns:
            utility_vector2 - a vector in which one component is replaced by the output_variable            
    '''
    usym_vec = utilities_vector[:]
    usym_vec[rank_position-1] = output_variable
    return usym_vec

def get_utilityscores_dataset(session, inp, outp, dataset):
    '''
        Evaluates the utilities that the network assigns to each data point.
        
        Args:
            session - tensorflow session
            inp  - tf variable for representing the network input
            outp - tf variable representing the network output
            dataset - list of ordered jf-vectors 
            
        Returns:
            U - a list of utility scores in the same format as dataset
    '''
    U = []
    N = len(dataset)
    input_dim = len(dataset[0][0])
    
    for i0 in range(N):
        M = len(dataset[i0])
        _utilities = []
        _inputs = []
        for i1 in range(M):
            current_dict = {}
            _inputs.append(np.asarray(dataset[i0][i1]).reshape((input_dim,1)))
            current_dict[inp.name] = _inputs[i1]
            u_current = session.run(outp, feed_dict=current_dict)
            _utilities.extend(u_current[0])    
        U.append(_utilities.copy())
    return U

def get_NLL_dataset(session, inp, outp, dataset):
    '''
        Evaluates the NLL of a whole data set.
        
        Args:
            session - tensorflow session
            inp  - tf variable for representing the network input
            outp - tf variable representing the network output
            dataset - list of ordered jf-vectors
            
        Returns:
            NLL - scalar value
    '''
    N = len(dataset)
    U = get_utilityscores_dataset(session, inp, outp, dataset)
    #print(U)
    nll_data = 0
    for i1 in range(N):
        _M = float(len(U[i1]))
        _nll = getNLLValue(U[i1])
        nll_data = nll_data + _nll/_M
    nll_data = nll_data / float(N)
    return nll_data

def get_NLL_dataset2(session, inp, outp, dataset):
    '''
        Evaluates the NLL of a whole data set.
        v2: not normalized across M
        Args:
            session - tensorflow session
            inp  - tf variable for representing the network input
            outp - tf variable representing the network output
            dataset - list of ordered jf-vectors
            
        Returns:
            NLL - scalar value
    '''
    N = len(dataset)
    U = get_utilityscores_dataset(session, inp, outp, dataset)
    #print(U)
    nll_data = 0
    for i1 in range(N):
        _nll = getNLLValue(U[i1])
        nll_data = nll_data + _nll
    nll_data = nll_data / float(N)
    return nll_data
