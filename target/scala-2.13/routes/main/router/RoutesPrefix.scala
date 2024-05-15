// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/alasd/OneDrive/Documents/MSC/Semester2/Team Project/Final/msc-team-project-master/conf/routes
// @DATE:Mon Mar 11 12:11:07 GMT 2024


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
