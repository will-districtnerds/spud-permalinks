package spud.security

class SpudUser {
		transient springSecurityService
	static transients = ["passwordConfirmation"]

	Long id

	String login
	String email
	String firstName
	String lastName
	String password

	String passwordSalt
	Date lastLoginAt
	Date lastRequestAt
	Date lastLoginIp
	Date currentLoginAt
	Integer failedLoginCount = 0
	Integer loginCount = 0
	String perishableToken
	String persistenceToken
	String singleAccessToken
	Date dateCreated
	Date lastUpdated
	String timeZone

	Boolean superAdmin = false


		static constraints = {
			login blank: false, nullable: false, unique: true
			email blank: false, nullable: false
			password nullable: false, blank:false
			passwordSalt nullable: false, blank: false
			timeZone nullable:true
			singleAccessToken nullable:true
			persistenceToken nullable:true
			perishableToken nullable:true
			currentLoginAt nullable:true
			lastLoginIp nullable:true
			lastLoginAt nullable:true
			lastRequestAt nullable:true
			firstName nullable:true
			lastName nullable:true
		}

		static mapping = {
			table 'spud_users'
			autoTimestamp true
			password column: 'crypted_password'
			dateCreated column: 'created_at'
			lastUpdated column: 'updated_at'
		}



	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			 encodePassword()
		}
	}

	protected void encodePassword() {
		if(!this.passwordSalt) {
			this.passwordSalt = login;
		}
		def digest = "${password}${passwordSalt}"

		for ( i in 1..20 ) {
			digest = springSecurityService.encodePassword(digest)
		}
		password = digest

	}

	def setPassword() {
		if(this.password && this.passwordConfirmation && this.password == this.passwordConfirmation)
		{
			//TODO: Set Crypted Password and Salt SHA256
		}
	}



	def updateSessionInfo(request) {
		this.lastRequestAt = new Date()
		this.save()
	}
}
