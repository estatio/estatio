SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------
--
-- drop Event.subjectBreakOptionId
--

ALTER TABLE [dbo].[Organisation] DROP COLUMN [fiscalCode]
GO

---------------------------
--
-- other constraints will be recreated when the app is restarted.
--