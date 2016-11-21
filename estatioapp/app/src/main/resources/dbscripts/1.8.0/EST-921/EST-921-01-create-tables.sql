

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

CREATE TABLE [dbo].[Partitioning](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[budgetId] [bigint] NOT NULL,
	[endDate] [date] NOT NULL,
	[startDate] [date] NOT NULL,
	[type] [varchar](255) NOT NULL,
	[version] [bigint] NOT NULL,
 CONSTRAINT [Partitioning_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [Partitioning_budget_type_startDate_UNQ] UNIQUE NONCLUSTERED
(
	[budgetId] ASC,
	[type] ASC,
	[startDate] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Partitioning]  WITH CHECK ADD  CONSTRAINT [Partitioning_FK1] FOREIGN KEY([budgetId])
REFERENCES [dbo].[Budget] ([id])
GO

ALTER TABLE [dbo].[Partitioning] CHECK CONSTRAINT [Partitioning_FK1]
GO

---------------------------
SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[PartitionItem](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[budgetItemId] [bigint] NOT NULL,
	[chargeId] [bigint] NOT NULL,
	[keyTableId] [bigint] NOT NULL,
	[partitioningId] [bigint] NOT NULL,
	[percentage] [decimal](19, 6) NOT NULL,
	[version] [bigint] NOT NULL,
 CONSTRAINT [PartitionItem_PK] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [PartitionItem_partitioning_charge_budgetItem_keyTable_UNQ] UNIQUE NONCLUSTERED
(
	[partitioningId] ASC,
	[chargeId] ASC,
	[budgetItemId] ASC,
	[keyTableId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[PartitionItem]  WITH CHECK ADD  CONSTRAINT [PartitionItem_FK1] FOREIGN KEY([budgetItemId])
REFERENCES [dbo].[BudgetItem] ([id])
GO

ALTER TABLE [dbo].[PartitionItem] CHECK CONSTRAINT [PartitionItem_FK1]
GO

ALTER TABLE [dbo].[PartitionItem]  WITH CHECK ADD  CONSTRAINT [PartitionItem_FK2] FOREIGN KEY([chargeId])
REFERENCES [dbo].[Charge] ([id])
GO

ALTER TABLE [dbo].[PartitionItem] CHECK CONSTRAINT [PartitionItem_FK2]
GO

ALTER TABLE [dbo].[PartitionItem]  WITH CHECK ADD  CONSTRAINT [PartitionItem_FK3] FOREIGN KEY([keyTableId])
REFERENCES [dbo].[KeyTable] ([id])
GO

ALTER TABLE [dbo].[PartitionItem] CHECK CONSTRAINT [PartitionItem_FK3]
GO

ALTER TABLE [dbo].[PartitionItem]  WITH CHECK ADD  CONSTRAINT [PartitionItem_FK4] FOREIGN KEY([partitioningId])
REFERENCES [dbo].[Partitioning] ([id])
GO

ALTER TABLE [dbo].[PartitionItem] CHECK CONSTRAINT [PartitionItem_FK4]
GO