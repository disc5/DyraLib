function [ w_learned ] = train_JFPL_MM( jftensor )
%Train_JFPL_MM Trains a JFPL model with Majorization-Minorization
%   
%   This version solves at each iteration a system of linear equations.
%   (Lange proposal: One Newton Step + Alternative for Inverting H)
%
%   Inputs:
%       jftensor - an ordered r x c x d tensor, where each row r represents an observation
%                       over c - many dyadic alternatives. Each alternative is
%                       described by a feature vector of dimensionality d.
%
%   Outputs:
%       w_learned - 1xd parameter vector
%
%   Note: incomplete entries in the jftensor must be expressed by Nans.
%         The tool to convert incomplete orderings to incomplete ordered
%         jf tensors is : [orderedIncompleteTensor] = createOrderedTensor(jfTensor, incompleteOrderings);
%
%   Testing:
%         nRankings = 220;
%         p = 3;
%         wgt = randn(p,1);
%         N = 5;
%         jfTensor = zeros(nRankings,N,p);
%         for i1 = 1 : nRankings
%             jfTensor(i1,:,:) = randn(N,p);
%         end
%         [orderings, skills] = getJFPLSamples5(jfTensor, wgt)
%         [orderedTensor] = createOrderedTensor(jfTensor, orderings)
%         [nll] = getLoss_JF_ordered_tensor_fast(jfTensor, wgt)
% 
%         [ w_learned0 ] = train_JFPL_MM( jfTensor )

% (C) 2017 Dirk Schaefer

    %% Init
    [~, ~, jfdim] = size(jftensor);
    weight0 = zeros(jfdim,1);
    v = weight0;
    
    tol = 0.0001;
    stepCnt = 0;

    %warning off all
    
    %% Steps:
    while true
        stepCnt = stepCnt + 1;
        f_last = getLoss_JF_ordered_tensor_fast(jftensor,v);
       
        %% Perform single Newton step
        [~, Grad, Hessian] = getJFPLMajorizingFunctionValue(jftensor, v, v);
        w_next = v - Hessian\Grad; % w_next = v - inv(Hessian)*(Grad);
        v = w_next;
        % This is equivalent to a system of lin eqiations: H deltaX = Grad
        % with  deltaX := w_next - v
        
        f_current = getLoss_JF_ordered_tensor_fast(jftensor,v);
        fprintf('f: %3.4f \n', f_current);
        if (abs(f_last - f_current) < tol) 
            fprintf('Minimum found. It took %d steps.\n', stepCnt);
            break;
        end
    end
    w_learned = v;
end

