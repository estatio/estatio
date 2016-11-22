/*
Cleanup: dropping stuff that should have been deleted in the past
 */

DROP TABLE dbo.Document
GO
DROP TABLE dbo.sysdiagrams
GO
DROP TABLE isispublishing.PublishedEvent
GO
DROP TABLE isissettings.UserSetting
GO
ALTER TABLE Brand DROP COLUMN parentBrandId
GO