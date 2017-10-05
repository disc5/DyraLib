function [meanKtau, vecKtau] =  getKtauPerformance(rankings1, rankings2)
    N = size(rankings1,1);
    vecKtau = zeros(N,1);
    for n=1:N
        vecKtau(n) = mykendall(rankings1(n,:),rankings2(n,:));
    end
    meanKtau = mean(vecKtau);
end