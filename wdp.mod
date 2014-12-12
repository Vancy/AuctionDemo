set N;
set ItemA; set ItemB; set ItemC; 
set Combinations within {N, ItemA, ItemB, ItemC};
set Limit_0 within {N, ItemA, ItemB, ItemC};
set Limit_1 within {N, ItemA, ItemB, ItemC};
set Limit_2 within {N, ItemA, ItemB, ItemC};

param bid {Combinations};
param size_ItemA > 0;
param size_ItemB > 0;
param size_ItemC > 0;

param number_of_bid_units_of_item_0 {Limit_0};
param number_of_bid_units_of_item_1 {Limit_1};
param number_of_bid_units_of_item_2 {Limit_2};

var x {N, ItemA, ItemB, ItemC}; binary;
maximize revenue:
		sum{(j,a0, a1, a2) in Combinations}  bid[j,a0, a1, a2] * x[j,a0, a1, a2];
subject to at_most_one_package {j in N}:
		sum{(j, a0, a1, a2) in Combinations}  x[j, a0, a1, a2] <=1;

subject to item_unit_limit_0:
	sum{(j, a0, a1, a2) in Limit_0} number_of_bid_units_of_item_0[j, a0, a1, a2] * x[j, a0, a1, a2] <= size_ItemA;

subject to item_unit_limit_1:
	sum{(j, a0, a1, a2) in Limit_1} number_of_bid_units_of_item_1[j, a0, a1, a2] * x[j, a0, a1, a2] <= size_ItemB;

subject to item_unit_limit_2:
	sum{(j, a0, a1, a2) in Limit_2} number_of_bid_units_of_item_2[j, a0, a1, a2] * x[j, a0, a1, a2] <= size_ItemC;

