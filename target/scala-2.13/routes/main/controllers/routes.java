// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/alasd/OneDrive/Documents/MSC/Semester2/Team Project/Final/msc-team-project-master/conf/routes
// @DATE:Mon Mar 11 12:11:07 GMT 2024

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseGameScreenController GameScreenController = new controllers.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseGameScreenController GameScreenController = new controllers.javascript.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
  }

}
