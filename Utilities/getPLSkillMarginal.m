function [ Marginal ] = getPLSkillMarginal(skills, objectID, rankID )
%GETPLSKILLMARGINAL Summary of this function goes here
%   Detailed explanation goes here
    M = length(skills);
    b = rankID;
    L = b;
    a = objectID;
    [ partPerms ] = get1FixedKPermutation( M, b, L, a );

    % Apply formula
    N2 = size(partPerms,1);
    M2 = size(partPerms,2);

    Marginal = 0;
    allSkillsSum = sum(skills(:));
    for i1 = 1 : N2
        products = 1;
        for i2 = 1 : M2
            if (i2==1)          
                products = products * (skills(partPerms(i1,i2)) / allSkillsSum);
            else
                restDenom = allSkillsSum - sum(skills(partPerms(i1,1:(i2-1))));
                products = products * (skills(partPerms(i1,i2)) / restDenom);
            end
        end
        Marginal = Marginal + products;
    end

end

