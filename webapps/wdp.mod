set N;  # set of bidders
set A1; set A2; set A3; set A4;  # sets of units of an object
set Combinations within {N, A1, A2, A3, A4};
set Limit_1 within {N, A1, A2, A3, A4};
set Limit_2 within {N, A1, A2, A3, A4};
set Limit_3 within {N, A1, A2, A3, A4};
set Limit_4 within {N, A1, A2, A3, A4};

param bid {Combinations};
param sizeA1 > 0;
param sizeA2 > 0;
param sizeA3 > 0;
param sizeA4 > 0;
param number_of_bid_units_of_item_1 {Limit_1};
param number_of_bid_units_of_item_2 {Limit_2};
param number_of_bid_units_of_item_3 {Limit_3};
param number_of_bid_units_of_item_4 {Limit_4};
var x {N,A1,A2,A3,A4} binary;

maximize revenue:
 	sum{(j,a1,a2,a3,a4) in Combinations}  bid[j,a1,a2,a3,a4] * x[j,a1,a2,a3,a4];

subject to at_most_one_package {j in N}:
	sum{(j,a1,a2,a3,a4) in Combinations} x[j,a1,a2,a3,a4] <= 1;

subject to item_unit_limit_1:
	sum{(j,a1,a2,a3,a4) in Limit_1} number_of_bid_units_of_item_1[j,a1,a2,a3,a4] * x[j,a1,a2,a3,a4] <= sizeA1;

subject to item_unit_limit_2:
	sum{(j,a1,a2,a3,a4) in Limit_2} number_of_bid_units_of_item_2[j,a1,a2,a3,a4] * x[j,a1,a2,a3,a4] <= sizeA2;

subject to item_unit_limit_3:
	sum{(j,a1,a2,a3,a4) in Limit_3} number_of_bid_units_of_item_3[j,a1,a2,a3,a4] * x[j,a1,a2,a3,a4] <= sizeA3;

subject to item_unit_limit_4:
	sum{(j,a1,a2,a3,a4) in Limit_4} number_of_bid_units_of_item_4[j,a1,a2,a3,a4] * x[j,a1,a2,a3,a4] <= sizeA4;


