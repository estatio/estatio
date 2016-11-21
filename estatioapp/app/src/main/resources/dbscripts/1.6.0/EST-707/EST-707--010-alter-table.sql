

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

ALTER TABLE LeaseTerm
DROP COLUMN excludedAmount, depositValue, depositType
GO
