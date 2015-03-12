

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

-- todo: use Jeroen's generic "drop constraints sproc instead"

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[Event]') AND name = N'Event_N49')
  DROP INDEX [Event_N49] ON [dbo].[Event] WITH ( ONLINE = OFF )
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[Event_FK1]') AND parent_object_id = OBJECT_ID(N'[dbo].[Event]'))
  ALTER TABLE [dbo].[Event] DROP CONSTRAINT [Event_FK1]
GO


ALTER TABLE [dbo].[Event] DROP COLUMN [subjectBreakOptionId]
GO


---------------------------

--
-- other constraints will be recreated when the app is restarted.
--