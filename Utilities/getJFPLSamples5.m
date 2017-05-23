% Primitive wrapper for getPLSample
% 
% The joint-feature vectors are provided in multiple sets, which are 
% realized as a tensor.
%
% Note: Permutations are drawn from vases instead of getting them by
% building the complete probability distribution over the rankings.
% 2016-03
function [orderings, skills] = getJFPLSamples5(jfTensor, w)
    numSamples = size(jfTensor,1);
    M = size(jfTensor,2);          % nAlternatives
    orderings = zeros(numSamples,M);
    skills = zeros(numSamples,M);
    for i1 = 1 : numSamples
        currentJfFeat = squeeze(jfTensor(i1,:,:));
        currentSkills = exp(w'*currentJfFeat'); 
        skills(i1,:) = currentSkills;
        if (size(jfTensor,3)==1) % fix, if third dimension is p=1, 2017-1
            [orderings(i1,:)] = getPLSampleFromVase(currentSkills');
        else
            [orderings(i1,:)] = getPLSampleFromVase(currentSkills);
        end
        
    end
end