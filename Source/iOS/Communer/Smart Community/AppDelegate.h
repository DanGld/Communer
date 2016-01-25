//
//  AppDelegate.h
//  Smart Community
//
//  Created by oren shany on 7/19/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//
@import CoreLocation;
@import SystemConfiguration;
@import AVFoundation;
@import ImageIO;

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "LocationModel.h"
#import <Google/SignIn.h>
#import <Pushwoosh/PushNotificationManager.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate,CLLocationManagerDelegate, GIDSignInDelegate> {
    CLLocationManager *locationManager;
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) LocationModel * currentLocation;
@property (strong, nonatomic) NSString* countryCode;


@end

