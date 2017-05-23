%% Demo of PLNet (by Dirk Schaefer), 2016
% see paper: Plackett-Luce Networks for Dyad Ranking, LWDA 2016
clear all
addpath(genpath('../../Utilities'))

%% Load data set
trfilename = 'housing_dense_tr.txt';
tefilename = 'housing_dense_te.txt';
[trInstances, trRankings] = load_lr_xxl(['data/' trfilename]);
[teInstances, teRankings] = load_lr_xxl(['data/' tefilename]);

%% Conv to orderings format
trOrderings = getOrderings(trRankings);
teOrderings = getOrderings(teRankings);

% Choose 1-of-K encoding as vector representation for labels
labelFeatures = eye(6);

%% Data shaping (training and test sets)
[ Xtr ] = convertCDR2netpldata( trInstances, labelFeatures, trOrderings );
[ Xte ] = convertCDR2netpldata( teInstances, labelFeatures, teOrderings );

%% Initialize PLNet
clear net;
close all;
net = plnet([length(Xtr{1}(1,:)),10,1],0.1); % -> MLP with 3 layers, 30 nodes for the hidden layer

%% Train PLNet weights
net.SGD(Xtr, 40, 0.01)

%% Kendall's Tau performance on training set
trLogScores = net.getUtilityScoresOfDataset(Xtr);
trSkills = exp(trLogScores);
[~, netTrOrderings] = sort(trSkills,2,'Descend');
trKtauNet = getKtauPerformance(getRankings(netTrOrderings),getRankings(repmat(1:size(netTrOrderings,2),size(netTrOrderings,1),1)))

%% Kendall's Tau performance on test set
teLogScores = net.getUtilityScoresOfDataset(Xte);
teSkills = exp(teLogScores);
[~, netTeOrderings] = sort(teSkills,2,'Descend');
teKtauNet = getKtauPerformance(getRankings(netTeOrderings),getRankings(repmat(1:size(netTeOrderings,2),size(netTeOrderings,1),1)))
