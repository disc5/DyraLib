% Samples from the (discrete) probability distribution over permutations
% as defined by the Plackett-Luce Model ("drawing from vases")
% Input v: PL skills, length = number of labels
function [ ordering ] = getPLSampleFromVase(v)
    %% Process Input
    M = length(v);
    %% Naive approach:
    % Take normed skills as probabilites
    remainingSkills = v;
    ordering = zeros(1,M);
    for stage = 1 : M-1
        prob = remainingSkills./sum(remainingSkills);
        [foo,idx] = histc(rand(1,1),[0,cumsum(prob)]);
        ordering(stage) = idx;
        remainingSkills(idx) = 0;
    end
    ordering(M) = find(remainingSkills);

end

