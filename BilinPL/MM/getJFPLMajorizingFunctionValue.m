function [ gval, Grad, Hessian ] = getJFPLMajorizingFunctionValue (jftensor, u, v)
%GETJFPLMAJORIZINGFUNCTIONVALUE Calculates the majorizing function for the
%JFPL NLL objective.
%
% Note: this version ommits constants terms, s.t. is not usuable for
% illustration purposes. Check: get1DMajorizingFunction.m
%
%   Inputs:
%       jftensor - an ordered r x c x d tensor, where each row r represents an observation
%                       over c - many dyadic alternatives. Each alternative is
%                       described by a feature vector of dimensionality d.
%
%   Output:
%       gval - the function value
%
% Used in: 
%   PL/JointFeature_PL_2017/Majorization
%
% Changelog
%   2017-01-21 - Support for incomplete orderings.
%
% (C) 2017 DS

[numObs,numAlternatives,jfdim] = size(jftensor);


weightedUFeatures = ntimes(jftensor, u, 3, 1);
sum2 = nansum(nansum(weightedUFeatures,2));
rankedUFeatures = exp(weightedUFeatures);

weightedVFeatures = ntimes(jftensor, v, 3, 1);
rankedVFeatures = exp(weightedVFeatures);


usums = zeros(numObs, numAlternatives);
vsums = zeros(numObs, numAlternatives);
for j = 1 : numAlternatives
    usums(:,j) = (nansum(rankedUFeatures(:,j:end),2));
    vsums(:,j) = (nansum(rankedVFeatures(:,j:end),2));
end

qsums = usums./vsums;
sum1 = nansum(nansum(qsums,2));
gval = sum1 - sum2;

    if (nargout > 1)
        % Calculate the gradient of g with respect to u
        Grad = zeros(jfdim,1);
        for jd = 1 : jfdim
            uGradsums = zeros(numObs, numAlternatives);
            for j = 1 : numAlternatives
                uGradsums(:,j) = (nansum(bsxfun(@times, jftensor(:,j:end, jd), rankedUFeatures(:,j:end)),2));
            end
            qGradSums = uGradsums./vsums;
            gradSum1 = nansum(nansum(qGradSums,2));
            gradSum2 = nansum(nansum(jftensor(:,:,jd),2));
            Grad(jd) = gradSum1 - gradSum2;
        end
    end
    if (nargout > 2)
        % Calculate the Hessian of g with respect to u
        Hessian = zeros(jfdim,jfdim);
        for jd1 = 1 : jfdim
            for jd2 = jd1 : jfdim
                uHessSums = zeros(numObs, numAlternatives);
                for j = 1 : numAlternatives
                    jfCrossProd = bsxfun(@times, jftensor(:,j:end, jd1),jftensor(:,j:end, jd2));
                    uHessSums(:,j) = (nansum(bsxfun(@times, jfCrossProd, rankedUFeatures(:,j:end)),2));
                end
                qHessSums = uHessSums./vsums;
                HessSum = nansum(nansum(qHessSums,2));
                Hessian(jd1,jd2) = HessSum;
                Hessian(jd2,jd1) = HessSum;
            end
        end
        
    end
end


