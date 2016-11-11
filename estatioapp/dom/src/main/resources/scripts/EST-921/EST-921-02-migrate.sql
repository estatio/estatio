

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------
INSERT INTO [dbo].[Partitioning]
	([budgetId], [endDate], [startDate], [type], [version])
SELECT b.id, b.endDate, b.startDate, 'BUDGETED', 1
	FROM [dbo].[Budget] b

GO

SET IDENTITY_INSERT [dbo].[PartitionItem] ON
GO

INSERT INTO [dbo].[PartitionItem]
	([id], [budgetItemId], [chargeId], [keyTableId], [partitioningId], [percentage], [version])
SELECT alloc.id, alloc.budgetItemId, alloc.chargeId, alloc.keyTableId, p.id , alloc.percentage, alloc.version
FROM [dbo].[BudgetItemAllocation] alloc
INNER JOIN [dbo].[BudgetItem] bi on bi.id = alloc.budgetItemId
INNER JOIN [dbo].[Budget] b on b.id = bi.budgetId
INNER JOIN [dbo].[Partitioning] p on p.budgetId = b.id