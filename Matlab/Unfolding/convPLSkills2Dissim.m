function [ Dissim ] = convPLSkills2Dissim( Vmat )
%CONVSKILL2DISSIM Converts a PL skill matrix to a matrix of dissimilarities
%   This conversion uses the log-transform with [0,1] normalization.
%
%   This transform is based on the considerations of Luce61, Kranz67:
%   d_ij = log(1/v_ji) = log(1) - log(v_ji) = -log(v_ji)
%   see also Borg81, S.265, (18.13)
%   Note: There is no fixation of the v-scale ,s.t. v_i(i) = 1. (!)
%   
%   Input
%       Vmat - N x M matrix of skills
%
%   Output
%       Dissim - N x M matrix of dissimilarities
%
%  This code can be used in conjunction with smacofSym.
% (C) 2017 DIRK SCHAEFER

D_IJ = -log(Vmat);
sr_min = min(D_IJ(:));
sr_max = max(D_IJ(:));
Dissim = (D_IJ-sr_min)/(sr_max - sr_min);

end

