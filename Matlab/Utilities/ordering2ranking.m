% Converts an ordering into ranking notation (see Marden95)

% In case of incomplete orderings, the rank position of 
% the unkown items are set to -1. (Note: assigning the 
% last available rank is also reasonable).
%
% Changelog
% 2014-08-17: Extended to deal with incomplete orderings.
function [ranking]=ordering2ranking(order)
    len=length(order);
    ranking=-1.*ones(1,len);
    for i1=1:len
        item_id = order(i1);
        if (item_id > -1)
            ranking(item_id)=i1; 
        end
    end
end