%% Dyadic Unfolding Demo
clear all
load('vowel_results/vowel4unfolding_results2.mat');

%% =============================== M D S ================================
%% Preparations
allX = [trX;teX];
allSkills = [trSkills;teSkills];

%% Dissim X, Y
DX = pdist(allX);
DXS = squareform(DX);% Eucdli Dist = Dissimilarities
DXS = DXS/max(DXS(:)); % scale them btw [0,1]

DY = pdist(labelFeatures);
DYS = squareform(DY)% Dissimilarities
DYS = DYS/max(DYS(:)); % scale them btw [0,1]

%% Dissim X~Y
Delta = convPLSkills2Dissim(allSkills);     %% -> AKA Transformation t2 in Journal Paper (2017-05-21)

%% Create Super-Delta
SDeltaU = [DXS, Delta];
SDeltaL = [Delta', DYS];
SDelta = [SDeltaU;SDeltaL];

%% Perform scaling / calculate embedding
%[ Conf, stress ] = smacofSym( SDelta, 2  )
alpha0 = 0;   % X Dissim (Instances)
beta0 = 0       % Y Dissim (Labels)
gamma0 = 1

alpha = alpha0/(alpha0+beta0+gamma0);
beta = beta0/(alpha0+beta0+gamma0);
gamma = gamma0 /(alpha0+beta0+gamma0);
fprintf('Weights: alpha=%3.4f, beta=%3.4f, gamma=%3.4f \n',alpha,beta,gamma);
n = size(SDelta,1);
wghts = ones(n,n).*gamma;
[nDx,mDx] = size(DXS);
wghts(1:nDx,1:mDx) = alpha;
wghts(nDx+1:end, mDx+1:end) = beta;
[ Conf, stress ] = smacofSym2( SDelta, 2, wghts  );

%% =============================== V I S ================================

%% Split into X and Y parts aka ConfRow, ConfCol
nRows = size(SDeltaU,1);
nCols = size(SDeltaL,1);
ConfRow = Conf(1:nRows,:);
ConfCol = Conf(nRows+1:end,:);

%% plot
clf
plot(ConfRow(:,1),ConfRow(:,2),'^g');
hold on
plot(ConfCol(:,1),ConfCol(:,2),'or')
grid on
axis equal

