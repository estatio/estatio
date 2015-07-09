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

ALTER TABLE [dbo].[Event] DROP COLUMN [subjectBreakOptionId]
GO

---------------------------
--
-- other constraints will be recreated when the app is restarted.
--