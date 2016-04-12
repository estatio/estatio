

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

UPDATE [dbo].[Party]
SET [dbo].[Party].[fiscalCode] = [dbo].[Organisation].[fiscalCode]
FROM [dbo].[Party] INNER JOIN [dbo].[Organisation] ON [dbo].[Party].[id] = [dbo].[Organisation].[id]