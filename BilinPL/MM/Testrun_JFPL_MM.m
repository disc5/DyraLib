clear all
addpath(genpath('../../Utilities'))

%% Synthesizing incomplete orderings
% (tensor representation of multiple sets of dyads / joint features)
nRankings = 220;
p = 3;
wgt = randn(p,1);
N = 5;
jfTensor = zeros(nRankings,N,p);
for i1 = 1 : nRankings
    jfTensor(i1,:,:) = randn(N,p);
end
[orderings, skills] = getJFPLSamples5(jfTensor, wgt)
[incompleteOrderings]=createPartialOrderings3(orderings, 0.6, 2)
[orderedIncompleteTensor] = createOrderedTensor(jfTensor, incompleteOrderings);
[nll] = getLoss_JF_ordered_tensor_fast(orderedIncompleteTensor, wgt)

%% Training via MM
[ w_learned0 ] = train_JFPL_MM( orderedIncompleteTensor )

%% Comparing NLL values
nll0 = getLoss_JF_ordered_tensor_fast(orderedIncompleteTensor,w_learned0)
fprintf('Groundtruth vs Model NLL: %3.4f  / %3.4f \n', nll, nll0)


