function [ Conf, stress ] = smacofSym2( Delta, p,  wghts )
%SMACOFSYM Performs MDS using Smacof (stress minimization by means of
%majorization)
%   This is a matlab port of the smacofSym.R version.
%
%   Input
%       Delta - n x n matrix of dissimilarity values
%       p - dimensionality of the MDS space
%
%   Output
%       Conf - configurations
%
%   Note: in this version there is no error handling, no support for
%   missing values and no transformation of dhats
%
%   By Dirk Schaefer

%% Init
epsilon = 1e-6;
itmax = 10^4;
diss = Delta;
n = size(Delta,1);

relax = 1; % relaxation factor
nn = n * (n-1) / 2;

diss2 = wghts.*diss;
X = togersonScaling(diss2);
x = X(:,1:p); % keep p dimensions

trilWghts = tril(wghts,-1);

% Create first dhats (via normDissN)
Temp = tril(wghts,-1).*tril(diss,-1).^2;
trilDhat = tril(diss,-1).*sqrt(n*(n-1)/2)/sqrt(sum(Temp(:)));

% Right before the iterations
rowSums = sum(wghts,2); % mimics R func rowSums
w = diag(rowSums) - wghts;
v = pinv(w);

itel = 1;
dx= pdist(x);
dxs = squareform(dx);%
trilDxs = tril(dxs,-1);
lb = sum(sum(trilWghts .* trilDxs .* trilDhat)) / sum(sum(trilWghts.*trilDxs.^2));

x = lb.*x;                   % modify x with lb-factor
trilDxs = trilDxs .* lb;     % modify d with lb-factor

%% stress old
sold = sum(sum(trilWghts.*(trilDhat - trilDxs).^2)) / nn;  %stress (to be minimized in repeat loop)

% Main loop
while (true)
    if all(trilDxs(:) < eps)
        z = tril(ones(size(trilDxs)),-1);
    else
        z = tril(zeros(size(trilDxs)),-1);
    end
    b_low = (trilWghts .* trilDhat .* (1-z))./(trilDxs + z); 
    b_low(isnan(b_low)) = 0;
    %btmp = tri2full(b_low);
    btmp = b_low + b_low';
    r = sum(btmp,2);
    b = diag(r) - btmp;

    y = v*(b*x);  % apply Guttman transform denoted as \bar(Y) in the paper
    %y = v*x; % -> does  not work!
    
    y = x+relax.*(y-x);  % n \times p matrix of Guttman transformed distances x's

    de = pdist(y);
    des = squareform(de);%
    trilDes = tril(des,-1);

    %% left out: transform /change of dhats (TODO)
    
    snon = sum(sum(trilWghts.*(trilDhat-trilDes).^2))/nn; % stress "non-metric"
    
    if (((sold-snon) < epsilon) || (itel == itmax))
        break;
    end
    x = y; % update configurations
    trilDxs = trilDes;
    sold = snon;
    itel = itel + 1;
end

stress = sqrt(snon);
fprintf('Done. It took %d iterations. Final stress: %3.4f \n', itel, stress);


%% Output
Conf = y;

end

