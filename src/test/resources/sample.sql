--#getUnion
SELECT Txn_Date FROM Store_Information
UNION
SELECT Txn_Date FROM Internet_Sales;
--#getIntersect
SELECT Txn_Date FROM Store_Information
INTERSECT
SELECT Txn_Date FROM Internet_Sales;
--#getTotal
SELECT a1.Name, a1.Sales, SUM(a2.Sales) Running_Total
  FROM Total_Sales a1, Total_Sales a2
  WHERE a1.Sales <= a2.sales or (a1.Sales=a2.Sales and a1.Name = a2.Name)
GROUP BY a1.Name, a1.Sales
ORDER BY a1.Sales DESC, a1.Name DESC;