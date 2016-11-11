

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------
-- Only budgeted values need to be migrated. There are no audit values in the system
INSERT INTO [dbo].[BudgetItemValue]
	([budgetItemId], [date], [type], [value], [version])
SELECT bi.id, b.startDate, 'BUDGETED', bi.budgetedValue, 1
	FROM [dbo].[BudgetItem] bi
	INNER JOIN  [dbo].[Budget] b ON bi.budgetId = b.id