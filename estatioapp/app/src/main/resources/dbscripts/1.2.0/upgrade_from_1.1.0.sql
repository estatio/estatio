UPDATE FinancialAccount
	SET discriminator = 'org.estatio.dom.financial.bankaccount.BankAccount'
	WHERE discriminator = 'org.estatio.dom.financial.BankAccount'