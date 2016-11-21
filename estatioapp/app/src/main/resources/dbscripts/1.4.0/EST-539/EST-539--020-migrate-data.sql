

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

INSERT INTO [dbo].[EventSourceLink] (
  [eventId],
  [calendarName],
  [sourceIdentifier],
  [sourceObjectType]
  )
SELECT e.id, e.calendarName, convert(varchar, e.subjectBreakOptionId), 'org.estatio.dom.lease.breaks.BreakOption'
  FROM Event e
 WHERE e.subjectBreakOptionId IS NOT NULL

INSERT INTO [dbo].[EventSourceLinkForBreakOption] (
  [id],
  [breakOptionId]
)
  SELECT id,convert(bigint, sourceIdentifier)
  FROM [dbo].[EventSourceLink]
  WHERE sourceObjectType = 'org.estatio.dom.lease.breaks.BreakOption'

