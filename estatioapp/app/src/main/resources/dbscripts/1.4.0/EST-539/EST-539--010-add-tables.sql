

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO

---------------------------

CREATE TABLE [dbo].[EventSourceLink](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [calendarName] [varchar](254) NOT NULL,
  [eventId] [bigint] NOT NULL,
  [sourceIdentifier] [varchar](255) NOT NULL,
  [sourceObjectType] [varchar](255) NOT NULL,
  CONSTRAINT [EventSourceLink_PK] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
  CONSTRAINT [EventSourceLink_event_source_UNQ] UNIQUE NONCLUSTERED
    (
      [eventId] ASC,
      [sourceObjectType] ASC,
      [sourceIdentifier] ASC
    )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[EventSourceLink]  WITH CHECK ADD  CONSTRAINT [EventSourceLink_FK1] FOREIGN KEY([eventId])
REFERENCES [dbo].[Event] ([id])
GO

ALTER TABLE [dbo].[EventSourceLink] CHECK CONSTRAINT [EventSourceLink_FK1]
GO



---------------------------

CREATE TABLE [dbo].[EventSourceLinkForBreakOption](
  [id] [bigint] NOT NULL,
  [breakOptionId] [bigint] NOT NULL,
  CONSTRAINT [EventSourceLinkForBreakOption_PK] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[EventSourceLinkForBreakOption]  WITH CHECK ADD  CONSTRAINT [EventSourceLinkForBreakOption_FK1] FOREIGN KEY([id])
REFERENCES [dbo].[EventSourceLink] ([id])
GO

ALTER TABLE [dbo].[EventSourceLinkForBreakOption] CHECK CONSTRAINT [EventSourceLinkForBreakOption_FK1]
GO

ALTER TABLE [dbo].[EventSourceLinkForBreakOption]  WITH CHECK ADD  CONSTRAINT [EventSourceLinkForBreakOption_FK2] FOREIGN KEY([breakOptionId])
REFERENCES [dbo].[BreakOption] ([id])
GO

ALTER TABLE [dbo].[EventSourceLinkForBreakOption] CHECK CONSTRAINT [EventSourceLinkForBreakOption_FK2]
GO



