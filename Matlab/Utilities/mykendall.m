% Kendall's tau Implementation
% -> same results like corr(p_ranking,sampleGTLabels','type','Kendall');
%
% Note (DS), 2014-01-13
% This is not kendall's tau distance.
% This implementation is th Kendall tau rank correlation coefficient
% see http://en.wikipedia.org/wiki/Kendall_tau_rank_correlation_coefficient
function [kendall]=mykendall(pi,sigma)  
    M=length(pi);
    C=0;
    D=0;
    for i=1:M
        for j=i+1:M
            p=(pi(i)-pi(j))*(sigma(i)-sigma(j));
            if p>=0
                C=C+1;
            else
                D=D+1;
            end
        end
    end
    denom=M*(M-1)/2;
    kendall=(C-D)/denom;
end