

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

ALTER TABLE [dbo].[Party] ADD [fiscalCode] [varchar](255)
GO

ALTER TABLE [dbo].[Organisation] ADD [companyRegistration] [varchar](255)
GO