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

%% Training JFPL(BilinPL) via L-BFGS
weight0 = zeros(p,1);
options = optimoptions(@fminunc,'Algorithm','quasi-newton',... %'trust-region','quasi-newton',...
    'DerivativeCheck','on','GradObj','on',...,
    'Diagnostics','on','Display','iter-detailed','FunValCheck','on','Hessian','on');
[x,fval,exitflag,output, grad, hessian] = fminunc(@(x) getLoss_JF_ordered_tensor_fast(orderedIncompleteTensor, x), weight0, options)

%% Comparing NLL values
[fval, grad2, hess2] =  getLoss_JF_ordered_tensor_fast(orderedIncompleteTensor, x)
fprintf('Groundtruth vs Model NLL: %3.4f  / %3.4f \n', nll, fval)


