// write the BankAccount class here
class BankAccount (val deposited: Long = 0,
                   val withdrawn: Long = 0,
                   val balance: Long = deposited - withdrawn)