#import "LocalStorageBackup.h"

@implementation LocalStorageBackup

- (void)pluginInitialize {

}

- (void) save:(CDVInvokedUrlCommand*)command {
    NSString *appId = [command.arguments objectAtIndex:0];
    NSString *localStorageString = [command.arguments objectAtIndex:1];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *storageKey = [@"localStorageBackup:" stringByAppendingString:appId];

    [defaults setObject:localStorageString forKey:storageKey];

    // maybe not the most performant, but guarantees values are written to disk
    // if the app quits before iOS syncs
    // Only do it for main View app so we don't potentially lose session
    if ([appId isEqualToString:@"View"]) {
        [defaults synchronize];
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) load:(CDVInvokedUrlCommand*)command {
    NSString *appId = [command.arguments objectAtIndex:0];
    NSString *storageKey = [@"localStorageBackup:" stringByAppendingString:appId];
    NSString *localStorageString = [[NSUserDefaults standardUserDefaults] objectForKey:storageKey];

    CDVPluginResult* pluginResult;
    if (localStorageString == nil) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"{}"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                     messageAsString:localStorageString];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) clear:(CDVInvokedUrlCommand*)command {
    NSString *appId = [command.arguments objectAtIndex:0];
    NSString *storageKey = [@"localStorageBackup:" stringByAppendingString:appId];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:storageKey];

    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
                                callbackId:command.callbackId];
}

@end

