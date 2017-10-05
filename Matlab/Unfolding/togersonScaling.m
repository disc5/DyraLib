function [ X ] = togersonScaling( Delta )
%TOGERSONSCALING (aka classical MDS, Torgerson-Gower Scaling)
%   Details on the method can be found in Borg95, Chapter 12.
%   Inputs: 
%       Delta - NxN dissimilarity matrix, e.g. euclidean distances
%
%   Outputs:
%       X - an Nxp matrix of configurations, where p corresponds to the
%       maximal number of positive eigenvalues found.
%
%   (C) 2017, DS

D2 = Delta.^2;
n = size(Delta,1);
J = eye(n)-1/n.*ones(n,1)*ones(n,1)'; % Centering matrix
B = -0.5 * J * D2 * J; % double centering operation (12.2)

[Q,A] = eig(B,'vector');
[As, Aord] = sort(A,'descend');
posIdx = find(As>0);
Qplus = Q(:,Aord(posIdx));
Aplus = diag(As(posIdx));

X = Qplus*Aplus.^(0.5); % Step 4

end

