

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

----------------------------
CREATE SCHEMA "EST-539"
go

CREATE VIEW "EST-539".Event
AS
SELECT e.id, e.calendarName, e."date", e.notes, e."version",
    (SELECT eslfbo.breakOptionId
       FROM dbo.EventSourceLink esl
       JOIN dbo.EventSourceLinkForBreakOption eslfbo
         ON esl.id = eslfbo.id
      WHERE e.id = esl.eventId) AS subjectBreakOptionId
  FROM dbo.Event e
GO

SELECT *
  FROM dbo.Event t
  JOIN "EST-539".Event v
    ON t.id = v.id
WHERE t.subjectBreakOptionId != v.subjectBreakOptionId
GO
