#import "LocalStorageRetrieval.h"

@implementation LocalStoragePlugin

- (void)pluginInitialize {

}

- (void) save:(CDVInvokedUrlCommand*)command {
    id localStorageJSON = [command.arguments objectAtIndex:0];
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:localStorageJSON
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    
    
    CDVPluginResult* pluginResult;
    if (error != nil) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                         messageAsString:[NSString stringWithFormat:@"Error serializing localStorage JSON, error: %@", [error localizedDescription]]];
    } else {
        [[NSUserDefaults standardUserDefaults] setObject:jsonData forKey:@"localStorage"];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) load:(CDVInvokedUrlCommand*)command {
    NSData *jsonData = [[NSUserDefaults standardUserDefaults] objectForKey:@"localStorage"];
    NSError *error;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:jsonData
                                                         options:NSJSONReadingAllowFragments
                                                           error:&error];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:json];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end


