

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

CREATE TABLE [dbo].[BudgetItemValue](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[budgetItemId] [bigint] NOT NULL,
	[date] [date] NOT NULL,
	[type] [varchar](255) NOT NULL,
	[value] [decimal](19, 2) NOT NULL,
	[version] [bigint] NOT NULL,
 CONSTRAINT [BudgetItemValue_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [BudgetItemValue_budgetItem_date_type] UNIQUE NONCLUSTERED
(
	[budgetItemId] ASC,
	[date] ASC,
	[type] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[BudgetItemValue]  WITH CHECK ADD  CONSTRAINT [BudgetItemValue_FK1] FOREIGN KEY([budgetItemId])
REFERENCES [dbo].[BudgetItem] ([id])
GO

ALTER TABLE [dbo].[BudgetItemValue] CHECK CONSTRAINT [BudgetItemValue_FK1]
GO