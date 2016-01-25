//
//  AppDelegate.m
//  Smart Community
//
//  Created by oren shany on 7/19/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "AppDelegate.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import "DataManagement.h"
#import "Networking.h"
#import <AudioToolbox/AudioToolbox.h>
#import <Fabric/Fabric.h>
#import <Crashlytics/Crashlytics.h>

#define systemSoundID    1007

@import GoogleMaps;

@interface AppDelegate ()

@end

@implementation AppDelegate


-(void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
{
    switch (status) {
        case kCLAuthorizationStatusNotDetermined:
        case kCLAuthorizationStatusRestricted:
        case kCLAuthorizationStatusDenied:
        {
            // do some error handling
        }
            break;
        default:{
            [locationManager startUpdatingLocation];
        }
            break;
    }
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    [Fabric with:@[[Crashlytics class]]];

    //-----------PUSHWOOSH PART-----------
    // set custom delegate for push handling, in our case - view controller
    PushNotificationManager * pushManager = [PushNotificationManager pushManager];
    pushManager.delegate = self;
    
    // handling push on app start
    [[PushNotificationManager pushManager] handlePushReceived:launchOptions];
    
    // make sure we count app open in Pushwoosh stats
    [[PushNotificationManager pushManager] sendAppOpen];
    
    // register for push notifications!
    [[PushNotificationManager pushManager] registerForPushNotifications];
    
    [GMSServices provideAPIKey:@"AIzaSyDMVM_Ge2EV4a1W6-bKgl4q9tNQvcan6vU"];
    
    // set up app language , default is english
    if (![[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"]) {
    [[NSUserDefaults standardUserDefaults] setObject:@"english" forKey:@"appLanguage"];
    }
    
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.distanceFilter = kCLDistanceFilterNone;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    [locationManager startUpdatingLocation];
    if([locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]){
        [locationManager requestWhenInUseAuthorization];
    }else{
        [locationManager startUpdatingLocation];
    }
    
    NSString* jsonData = [[NSUserDefaults standardUserDefaults]
               objectForKey:@"jsonData"];
    if (jsonData) {
    DataManagement* dataManager = [DataManagement sharedInstance];
        if ([jsonData isKindOfClass:[NSDictionary class]])
            dataManager.user = (userModel*)[userModel objectForDictionary:jsonData];
        else  {
            dataManager.user = (userModel*)[userModel objectForJSON:jsonData];
        }
        
        dataManager.user.communities = [NSMutableArray arrayWithArray:dataManager.user.communities];
        for (int i=0; i<dataManager.user.communities.count; i++) {
            CommunityModel* community = (CommunityModel*)[CommunityModel objectForDictionary:(NSDictionary*)dataManager.user.communities[i]];
            community.messages = [NSMutableArray arrayWithArray:community.messages];
            for (int i=0; i<community.messages.count; i++) {
                EventOrMessageModel* message = community.messages[i];
                message = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:(NSDictionary*)message];
                message.participants = [NSMutableArray arrayWithArray:message.participants];
                for (int i=0; i<message.participants.count; i++) {
                    CommunityMemberModel* participant = message.participants[i];
                    participant = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)participant];
                    message.participants[i] = participant;
                }
                message.comments = [NSMutableArray arrayWithArray:message.comments];
                for (int i=0; i<message.comments.count; i++) {
                    CommentModel* comment = message.comments[i];
                    comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                    message.comments[i] = comment;
                }
                community.messages[i] = message;
            }
            community.contacts = [NSMutableArray arrayWithArray:community.contacts];
            for (int i=0; i<community.contacts.count; i++) {
                CommunityContactModel* contact = community.contacts[i];
                contact = (CommunityContactModel*)[CommunityContactModel objectForDictionary:(NSDictionary*)contact];
                community.contacts[i] = contact;
            }
            community.privateQA = [NSMutableArray arrayWithArray:community.privateQA];
            for (int i=0; i<community.privateQA.count; i++) {
                postModel* qa = community.privateQA[i];
                qa = (postModel*)[postModel objectForDictionary:(NSDictionary*)qa];
                //qa.communityMember = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)qa.communityMember];
                qa.comments = [NSMutableArray arrayWithArray:qa.comments];
                for (int i=0; i<qa.comments.count; i++) {
                    CommentModel* comment = qa.comments[i];
                    comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                    qa.comments[i] = comment;
                }
                community.privateQA[i] = qa;
            }
            community.publicQA = [NSMutableArray arrayWithArray:community.publicQA];
            for (int i=0; i<community.publicQA.count; i++) {
                postModel* qa = community.publicQA[i];
                qa = (postModel*)[postModel objectForDictionary:(NSDictionary*)qa];
                //qa.communityMember = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)qa.communityMember];
                qa.comments = [NSMutableArray arrayWithArray:qa.comments];
                for (int i=0; i<qa.comments.count; i++) {
                    CommentModel* comment = qa.comments[i];
                    comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                    qa.comments[i] = comment;
                }
                community.publicQA[i] = qa;
            }
            community.communityPosts = [NSMutableArray arrayWithArray:community.communityPosts];
            for (int i=0; i<community.communityPosts.count; i++) {
                postModel* post = community.communityPosts[i];
                post = (postModel*)[postModel objectForDictionary:(NSDictionary*)post];
               // post.communityMember = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)post.communityMember];
                post.comments = [NSMutableArray arrayWithArray:post.comments];
                for (int i=0; i<post.comments.count; i++) {
                    CommentModel* comment = post.comments[i];
                    comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                    post.comments[i] = comment;
                }
                community.communityPosts[i] = post;
            }
            community.facilities = [NSMutableArray arrayWithArray:community.facilities];
            for (int i=0; i<community.facilities.count; i++) {
                FacilityOrActivityModel* facilty = community.facilities[i];
                facilty = (FacilityOrActivityModel*)[FacilityOrActivityModel objectForDictionary:(NSDictionary*)facilty];
               // facilty.communityContact = (CommunityContactModel*)[CommunityContactModel objectForDictionary:(NSDictionary*)facilty.communityContact];
                facilty.events = [NSMutableArray arrayWithArray:facilty.events];
                for (int i=0; i<facilty.events.count; i++) {
                    EventOrMessageModel* event = facilty.events[i];
                    event = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:(NSDictionary*)event];
                    event.participants = [NSMutableArray arrayWithArray:event.participants];
                    for (int i=0; i<event.participants.count; i++) {
                        CommunityMemberModel* participant = event.participants[i];
                        participant = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)participant];
                        event.participants[i] = participant;
                    }
                    event.comments = [NSMutableArray arrayWithArray:event.comments];
                    for (int i=0; i<event.comments.count; i++) {
                        CommentModel* comment = event.comments[i];
                        comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                        event.comments[i] = comment;
                    }
                    event.date = event.sTime;
                    facilty.events[i] = event;
                }
                

                
                community.facilities[i] = facilty;
            }
            
            community.dailyEvents = [NSMutableArray arrayWithArray:community.dailyEvents];
            for (int i=0; i<community.dailyEvents.count; i++) {
                dailyEventsModel* dailyEvent = community.dailyEvents[i];
                dailyEvent = (dailyEventsModel*)[dailyEventsModel objectForDictionary:(NSDictionary*)dailyEvent];
                dailyEvent.prayers = [NSMutableArray arrayWithArray:dailyEvent.prayers];
                for (int i=0; i<dailyEvent.prayers.count; i++) {
                    PrayerDetailsModel* prayer = dailyEvent.prayers[i];
                    prayer = (PrayerDetailsModel*)[PrayerDetailsModel objectForDictionary:(NSDictionary*)prayer];
                    prayer.prayers = [NSMutableArray arrayWithArray:prayer.prayers];
                    for (int i=0; i<prayer.prayers.count; i++) {
                        SmallPrayer* smallPrayer = prayer.prayers[i];
                        smallPrayer = (SmallPrayer*)[SmallPrayer objectForDictionary:(NSDictionary*)smallPrayer];
                        prayer.prayers[i] = smallPrayer;
                    }
                     prayer.smallPrayers = [NSMutableArray arrayWithArray:prayer.smallPrayers];
                    for (int i=0; i<prayer.smallPrayers.count; i++) {
                        SmallPrayer* smallPrayer = prayer.smallPrayers[i];
                        smallPrayer = (SmallPrayer*)[SmallPrayer objectForDictionary:(NSDictionary*)smallPrayer];
                        prayer.smallPrayers[i] = smallPrayer;
                    }
                    dailyEvent.prayers[i] = prayer;
                }
                dailyEvent.events = [NSMutableArray arrayWithArray:dailyEvent.events];
                for (int i=0; i<dailyEvent.events.count; i++) {
                    EventOrMessageModel* event = dailyEvent.events[i];
                    event = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:(NSDictionary*)event];
                    event.participants = [NSMutableArray arrayWithArray:event.participants];
                    for (int i=0; i<event.participants.count; i++) {
                        CommunityMemberModel* participant = event.participants[i];
                        participant = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)participant];
                        event.participants[i] = participant;
                    }
                    event.comments = [NSMutableArray arrayWithArray:event.comments];
                    for (int i=0; i<event.comments.count; i++) {
                        CommentModel* comment = event.comments[i];
                        comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                        event.comments[i] = comment;
                    }
                    event.date = event.sTime;
                    dailyEvent.events[i] = event;
                }
                dailyEvent.activities = [NSMutableArray arrayWithArray:dailyEvent.activities];
                for (int i=0; i<dailyEvent.activities.count; i++) {
                    FacilityOrActivityModel* facilty = dailyEvent.activities[i];
                    facilty = (FacilityOrActivityModel*)[FacilityOrActivityModel objectForDictionary:(NSDictionary*)facilty];
                    dailyEvent.activities[i] = facilty;
                    facilty.events = [NSMutableArray arrayWithArray:facilty.events];
                    for (int i=0; i<facilty.events.count; i++) {
                        EventOrMessageModel* event = facilty.events[i];
                        event = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:(NSDictionary*)event];
                        event.participants = [NSMutableArray arrayWithArray:event.participants];
                        for (int i=0; i<event.participants.count; i++) {
                            CommunityMemberModel* participant = event.participants[i];
                            participant = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)participant];
                            event.participants[i] = participant;
                        }
                        event.comments = [NSMutableArray arrayWithArray:event.comments];
                        for (int i=0; i<event.comments.count; i++) {
                            CommentModel* comment = event.comments[i];
                            comment= (CommentModel*)[CommentModel objectForDictionary:(NSDictionary*)comment];
                            event.comments[i] = comment;
                        }
                        event.date = event.sTime;
                        facilty.events[i] = event;
                    }
                }
                
                community.dailyEvents[i] = dailyEvent;
            }
            
                community.members = [NSMutableArray arrayWithArray:community.members];
                for (int i=0; i<community.members.count; i++) {
                    CommunityMemberModel* member = community.members[i];
                    member = (CommunityMemberModel*)[CommunityMemberModel objectForDictionary:(NSDictionary*)member];
                    community.members[i] = member;
                }
                
                community.eventImages = [NSMutableArray arrayWithArray:community.eventImages];
                for (int i=0; i<community.eventImages.count; i++) {
                    EventMediaModel* eventMedia = community.eventImages[i];
                    eventMedia = (EventMediaModel*)[EventMediaModel objectForDictionary:(NSDictionary*)eventMedia];
                    eventMedia.media = [NSMutableArray arrayWithArray:eventMedia.media];
                    for (int i=0; i<eventMedia.media.count; i++) {
                        ImageModel* media = eventMedia.media[i];
                        media = (ImageModel*)[ImageModel objectForDictionary:(NSDictionary*)media];
                        eventMedia.media[i] = media;
                    }
                    community.eventImages[i] = eventMedia;
                }

                community.securityEvents = [NSMutableArray arrayWithArray:community.securityEvents];
                for (int i=0; i<community.securityEvents.count; i++) {
                    SecurityEventModel* securityEvent = community.securityEvents[i];
                    securityEvent = (SecurityEventModel*)[SecurityEventModel objectForDictionary:(NSDictionary*)securityEvent];
                    community.securityEvents[i] = securityEvent;
                }
                
                community.guideCategories = [NSMutableArray arrayWithArray:community.guideCategories];
                for (int i=0; i<community.guideCategories.count; i++) {
                    GuideCategoryModel* guideCategory = community.guideCategories[i];
                    guideCategory = (GuideCategoryModel*)[GuideCategoryModel objectForDictionary:(NSDictionary*)guideCategory];
                    community.guideCategories[i] = guideCategory;
                }
            dataManager.user.communities[i] = community;
        }
    }
    
    NSError* configureError;
    [[GGLContext sharedInstance] configureWithError: &configureError];
    NSAssert(!configureError, @"Error configuring Google services: %@", configureError);
    
    [GIDSignIn sharedInstance].delegate = self;
    
    
    return [[FBSDKApplicationDelegate sharedInstance] application:application
                                    didFinishLaunchingWithOptions:launchOptions];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    if ([url.description containsString:@"com.facebook.sdk"]) {
    return [[FBSDKApplicationDelegate sharedInstance] application:application
                                                          openURL:url
                                                sourceApplication:sourceApplication
                                                       annotation:annotation];
    }
    else {
    return [[GIDSignIn sharedInstance] handleURL:url
                        sourceApplication:sourceApplication
                               annotation:annotation];
    }
}

- (void)signIn:(GIDSignIn *)signIn
didSignInForUser:(GIDGoogleUser *)user
     withError:(NSError *)error {
    [DataManagement sharedInstance].user.name = user.profile.name;
    [DataManagement sharedInstance].user.email = user.profile.email;
    if (user.profile.hasImage) {
        NSUInteger dimension = 500;
        NSURL *imageURL = [user.profile imageURLWithDimension:dimension];
        [DataManagement sharedInstance].user.imageUrl = [NSString stringWithFormat:@"%@" , imageURL];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"googleUserDetails"
                                                        object:nil
                                                      userInfo:nil];
    
}

- (void)signIn:(GIDSignIn *)signIn
didDisconnectWithUser:(GIDGoogleUser *)user
     withError:(NSError *)error {
    // Perform any operations when the user disconnects from app here.
    // ...
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    [[Networking sharedInstance] fetchNewData];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    _currentLocation = [[LocationModel alloc]init];
    _currentLocation.coords = [NSString stringWithFormat:@"%f:%f" , newLocation.coordinate.latitude , newLocation.coordinate.longitude];
    [locationManager stopUpdatingLocation];
    
    CLGeocoder * geoCoder = [[CLGeocoder alloc] init];
    [geoCoder reverseGeocodeLocation:newLocation
                   completionHandler:^(NSArray *placemarks, NSError *error) {
                       if (placemarks.count>0) {
                       CLPlacemark *placemark = placemarks[0];
                       NSArray *lines = placemark.addressDictionary[ @"FormattedAddressLines"];
                       NSString *addressString = [lines componentsJoinedByString:@" "];
                           _currentLocation.title = addressString;
                       }
                   }];
}

// system push notification registration success callback, delegate to pushManager
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [[PushNotificationManager pushManager] handlePushRegistration:deviceToken];
}

// system push notification registrati\on error callback, delegate to pushManager
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [[PushNotificationManager pushManager] handlePushRegistrationFailure:error];
}

// system push notifications callback, delegate to pushManager
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    [[PushNotificationManager pushManager] handlePushReceived:userInfo];
    AudioServicesPlaySystemSound (systemSoundID);
}

- (void) onPushAccepted:(PushNotificationManager *)pushManager withNotification:(NSDictionary *)pushNotification {
    NSLog(@"Push notification received");
}

@end
