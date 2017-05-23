% getLoss_JF_ordered_tensor_fast Calculates the NLL of the JFPL objective.
%
% Note: Uses ntimes from Bro's N-Way Toolbox 
%
% Changelog:
%   2017-01-21 - Support for incomplete ordered jftensors (missing values indicated with Nans)
%   2017-01-07 - Gradient and Hessian added
%   2014-02-05 - Fast Version for JF-PL Loss Function with ordered input tensor
function [nll, Grad, Hessian] = getLoss_JF_ordered_tensor_fast(jftensor, weight)

    [numInst,numLabels,jfdim] = size(jftensor);

    weightedFeatures = ntimes(jftensor, weight, 3, 1);
    sum1 = nansum(nansum(weightedFeatures,2));
    rankedFeatures = exp(weightedFeatures);

    logsums = zeros(numInst, numLabels);
    for j = 1:numLabels
        logsums(:,j) = log(nansum(rankedFeatures(:,j:end),2));
    end
    logsums_backup = logsums;
    logsums(find(logsums==-Inf)) = 0;
    
    sum2 = sum(sum(logsums,2));
    loglikelihood = sum1 - sum2;
    nll = -loglikelihood;

     if (nargout > 1)
        % Calculate gradient
        Grad = zeros(jfdim,1);
        for jd = 1 : jfdim
            uGradsums = zeros(numInst, numLabels);
            for j = 1 : numLabels
                uGradsums(:,j) = (nansum(bsxfun(@times, jftensor(:,j:end, jd), rankedFeatures(:,j:end)),2));
            end
            qGradSums = uGradsums./exp(logsums_backup);
            gradSum1 = nansum(nansum(qGradSums,2));
            gradSum2 = nansum(nansum(jftensor(:,:,jd),2));
            Grad(jd) = gradSum1 - gradSum2;
        end
     end
     
     if (nargout > 2)
        % Calculate Hessian
        Hessian = zeros(jfdim, jfdim);
        gSums = exp(logsums_backup);
        for jd1 = 1 : jfdim
            for jd2 = jd1 : jfdim
                hdSums = zeros(numInst, numLabels);
                hSums = zeros(numInst, numLabels);
                gdSums = zeros(numInst, numLabels);
                for j = 1 : numLabels
                    jfCrossProd = bsxfun(@times, jftensor(:,j:end, jd1),jftensor(:,j:end, jd2));
                    hdSums(:,j) = (nansum(bsxfun(@times, jfCrossProd, rankedFeatures(:,j:end)),2));
                    hSums(:,j) = (nansum(bsxfun(@times, jftensor(:,j:end, jd1), rankedFeatures(:,j:end)),2));
                    gdSums(:,j) = (nansum(bsxfun(@times, jftensor(:,j:end, jd2), rankedFeatures(:,j:end)),2));
                end
             
                num = hdSums.*gSums - hSums.*gdSums;
                denom = gSums.^2;
                qHessSums = num./denom;
                       
                HessSum = nansum(nansum(qHessSums,2));
                Hessian(jd1,jd2) = HessSum;
                Hessian(jd2,jd1) = HessSum;
            end
        end
        
     end
    
end