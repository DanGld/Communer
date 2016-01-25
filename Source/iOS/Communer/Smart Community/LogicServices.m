//
//  LogicServices.m
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "LogicServices.h"
#import "EventOrMessageModel.h"

@implementation LogicServices

+ (instancetype)sharedInstance
{
    static LogicServices *myInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        myInstance = [[self alloc] init];
    });
    return myInstance;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        _dataManager = [DataManagement sharedInstance];
    }
    return self;
}

- (NSArray*)getAllCommunitiesActivities {
    NSMutableArray* activities = [NSMutableArray array];
    NSArray* communitiesArr = _dataManager.user.communities;
    for (CommunityModel* community in communitiesArr) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (dailyEventsModel* dailyEventModel in community.dailyEvents) {
            if (dailyEventModel.activities) {
                return dailyEventModel.activities;
            }
          }
        }
    }
    return activities;
}

- (NSArray*)getAllCommunityFacilities {
    NSMutableArray* facilities = [NSMutableArray array];
    NSArray* communitiesArr = _dataManager.user.communities;
    for (CommunityModel* community in communitiesArr) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            if (community.facilities) {
                return community.facilities;
            }
        }
    }
    return facilities;
}



-(CommunityModel*)getCurrentCommunity {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            return community;
        }
    }
    return nil;
}


- (dailyEventsModel*)getCommunityDailyEventsForDate:(NSDate*)date {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"dd/MM/yyyy"];
    NSString* dateString = [dateFormatter stringFromDate:date];
    
    NSArray* communitiesArr = _dataManager.user.communities;
    for (CommunityModel* community in communitiesArr) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (dailyEventsModel* dailyEventModel in community.dailyEvents) {
                if ([dailyEventModel.date isEqualToString:dateString]) {
                        return dailyEventModel;
                    }
          }
        }
    }
    return nil;
}

- (NSArray*)getAllCommunityEvents {
    NSMutableArray* events = [NSMutableArray array];
    NSArray* communitiesArr = _dataManager.user.communities;
    for (CommunityModel* community in communitiesArr) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (dailyEventsModel* dailyEventModel in community.dailyEvents) {
            if (dailyEventModel.events) {
                for (EventOrMessageModel* event in dailyEventModel.events) {
                   // if (event.isActive) {
                        [events addObject:event];
                  //  }
                }
           // [events addObjectsFromArray:dailyEventModel.events];
            }
        }
      }
    }
    return events;
}

- (PrayerDetailsModel*)getAllPrayersForDate:(NSDate*)date {
    NSMutableArray* communities = _dataManager.user.communities;
    for (CommunityModel* community in communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (dailyEventsModel* dailyEventModel in community.dailyEvents) {
        for (PrayerDetailsModel* prayerDetails in dailyEventModel.prayers) {
            if ([self isSameDayWithDate1:[NSDate dateWithTimeIntervalSince1970:prayerDetails.date/1000] date2:date]) {
                return prayerDetails;
            }
        }
        }
     }
    }
    return nil;
}

- (NSArray*)getNextPrayersForDate:(NSDate*)date {
    NSMutableArray* slicedPrayersArr = [NSMutableArray array];
    PrayerDetailsModel* prayers = [self getAllPrayersForDate:date];
    for (SmallPrayer* prayer in prayers.smallPrayers) {
        if (prayer.time.length >3) {
        NSDate *date = [NSDate date];
        NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier: NSCalendarIdentifierGregorian];
        NSDateComponents *components = [gregorian components: NSUIntegerMax fromDate: date];
        [components setHour: [[prayer.time substringToIndex:1] intValue]];
        [components setMinute: [[prayer.time substringFromIndex:3] intValue]];
        NSDate *prayerDate = [gregorian dateFromComponents: components];
        if ([prayerDate compare:[NSDate date]] == NSOrderedDescending) {
            [slicedPrayersArr addObject:prayer];
            }
        }
    }
    
    slicedPrayersArr = prayers.smallPrayers;
    NSArray *sortedArray = [slicedPrayersArr sortedArrayUsingComparator: ^(SmallPrayer* p1, SmallPrayer *p2) {
        return [p1.time compare:p2.time];
    }];
    
    return sortedArray;
}

- (NSArray*)getLastTwoMessages {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            NSArray *sortedArray = [community.messages sortedArrayUsingComparator: ^(EventOrMessageModel* m1, EventOrMessageModel *m2) {
                return [[[NSDate alloc]initWithTimeIntervalSince1970:m2.sTime/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:m1.sTime/1000]];
            }];
        if (sortedArray.count>1)
        return [sortedArray subarrayWithRange:NSMakeRange(0, 2)];
        
        return sortedArray;
        }
    }
    return nil;
}

- (NSArray*)getAllGeneralMessages {
    NSMutableArray* messages = [NSMutableArray array];
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (EventOrMessageModel* message in community.messages) {
            if ([message.type isEqualToString:@"general"]) {
                [messages addObject:message];
            }
        }
      }
    }
    
    NSArray *sortedArray = [messages sortedArrayUsingComparator: ^(EventOrMessageModel* m1, EventOrMessageModel *m2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:m2.sTime/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:m1.sTime/1000]];
    }];
    return sortedArray;
}

- (NSArray*)getAllPersonalMessages {
    NSMutableArray* messages = [NSMutableArray array];
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (EventOrMessageModel* message in community.messages) {
            if ([message.type isEqualToString:@"personal"]) {
                [messages addObject:message];
            }
        }
      }
    }
    
    NSArray *sortedArray = [messages sortedArrayUsingComparator: ^(EventOrMessageModel* m1, EventOrMessageModel *m2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:m2.sTime/1000]  compare:[[NSDate alloc]initWithTimeIntervalSince1970:m1.sTime/1000] ];
    }];
    return sortedArray;
}

- (NSArray*)getLast10Images {
    NSMutableArray* lastImages = [NSMutableArray array];
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        for (EventMediaModel* eventMedia in community.eventImages) {
            for (ImageModel* image in eventMedia.media) {
                if (lastImages.count <10) {
                [lastImages addObject:image.link];
                }
            }
        }
        }
    }
    return lastImages;
}

- (NSArray*)getAllImagesEvents {
    NSMutableArray* eventImages = [NSMutableArray array];
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        [eventImages addObjectsFromArray:community.eventImages];
        }
    }
    return eventImages;
}

- (NSArray*)getAllGuidesCategories {
    NSMutableArray* guideCats = [NSMutableArray array];
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
        [guideCats addObjectsFromArray:community.guideCategories];
        }
    }
    return guideCats;
}

-(NSArray*)getAllCommunityPublicQuestions {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            return community.publicQA;
        }
    }
    return nil;
}

-(NSArray*)getAllCommunityPrivateQuestions {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            return community.privateQA;
        }
    }
    return nil;
}

-(postModel*)getLastCommunityPost {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            NSMutableArray* communityPosts = [NSMutableArray arrayWithArray:[community.communityPosts sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
                return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000]  compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000] ];
            }]];
            if (communityPosts.count>0)
            return communityPosts[0];
        }
    }
    return nil;
}

-(postModel*)getLastPublicQuestion {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            NSMutableArray* publicQA = [NSMutableArray arrayWithArray:[community.publicQA sortedArrayUsingComparator: ^(postModel *d1, postModel *d2) {
                return [[[NSDate alloc]initWithTimeIntervalSince1970:d2.cDate/1000]  compare:[[NSDate alloc]initWithTimeIntervalSince1970:d1.cDate/1000] ];
            }]];
            if (publicQA.count>0)
            return publicQA[0];
        }
    }
    return nil;
}


-(NSArray*)getSecurityReportArray {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            return community.securityEvents;
        }
    }
    return nil;
}


-(void)addCommunityPrivateQuestion:(postModel*)question {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
             [community.privateQA addObject:question];
            return;
        }
    }
}

-(void)addCommunityPublicQuestion:(postModel*)question {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            [community.publicQA addObject:question];
            return;
        }
    }
}

-(void)addCommunityPrivateQuestionComment:(CommentModel*)comment ForQuestionID:(NSString*)questionID {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            for (postModel* question in community.privateQA) {
                if ([question.postID isEqualToString:questionID]) {
                    [question.comments addObject:comment];
                    return;
                }
            }
        }
    }
}

-(void)addCommunityPublicQuestionComment:(CommentModel*)comment ForQuestionID:(NSString*)questionID {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            for (postModel* question in community.publicQA) {
                if ([question.postID isEqualToString:questionID]) {
                    [question.comments addObject:comment];
                    return;
                }
            }
        }
    }
}

-(void)addCommunityPost:(postModel*)post {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            [community.communityPosts addObject:post];
            return;
        }
    }
}

-(void)addCommunityPostComment:(CommentModel*)comment ForPostID:(NSString*)postID {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            for (postModel* post in community.communityPosts) {
                if ([post.postID isEqualToString:postID]) {
                    [post.comments addObject:comment];
                    return;
                }
            }
        }
    }
}

-(void)addNewMessage:(EventOrMessageModel*)message {
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            for (int i=0 ; i<community.messages.count ; i++) {
                if ([((EventOrMessageModel*)community.messages[i]).eventID isEqualToString:message.eventID])
                {
                    community.messages[i] = message;
                    NSDictionary* jsonString = [[DataManagement sharedInstance].user objectToDictionary];
                    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
                    [[NSUserDefaults standardUserDefaults] synchronize];
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                                        object:nil
                                                                      userInfo:nil];
                    return;
                }
            }
            [community.messages addObject:message];
            NSDictionary* jsonString = [[DataManagement sharedInstance].user objectToDictionary];
            [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                                object:nil
                                                              userInfo:nil];
        }
    }
}

-(void)addNewEvents:(NSMutableArray*)events {
    if (events.count>0) {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"dd/MM/yyyy"];
    NSString* eventDateString = [dateFormatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:((EventOrMessageModel*)events[0]).sTime/1000]];
    
    for (CommunityModel* community in _dataManager.user.communities) {
        if ([community.communityID isEqualToString:_dataManager.currentCommunityID]) {
            for (dailyEventsModel* dailyEvent in community.dailyEvents) {
                if ([dailyEvent.date isEqualToString:eventDateString]) {
                    for (EventOrMessageModel* newEvent in events) {
                    for (int i=0; i<dailyEvent.events.count; i++) {
                        if ([((EventOrMessageModel*)dailyEvent.events[i]).eventID isEqualToString:newEvent.eventID])
                        {
                            dailyEvent.events[i] = newEvent;
                            NSDictionary* jsonString = [[DataManagement sharedInstance].user objectToDictionary];
                            [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
                            [[NSUserDefaults standardUserDefaults] synchronize];

                            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                                                object:nil
                                                                              userInfo:nil];
                            return;
                        }
                      }
                    [dailyEvent.events addObject:newEvent];
                    NSDictionary* jsonString = [[DataManagement sharedInstance].user objectToDictionary];
                    [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
                    [[NSUserDefaults standardUserDefaults] synchronize];
                    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                                        object:nil
                                                                      userInfo:nil];
                    }
                }
                }
            }
        }
    }
}




- (BOOL)isSameDayWithDate1:(NSDate*)date1 date2:(NSDate*)date2 {
    NSCalendar* calendar = [NSCalendar currentCalendar];
    unsigned unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit;
    if ([date1 isKindOfClass:[NSDate class]] && [date2 isKindOfClass:[NSDate class]]) {
    NSDateComponents* comp1 = [calendar components:unitFlags fromDate:date1];
    NSDateComponents* comp2 = [calendar components:unitFlags fromDate:date2];
    
    return [comp1 day]   == [comp2 day] &&
    [comp1 month] == [comp2 month] &&
    [comp1 year]  == [comp2 year];
    }
    else {
        return false;
    }
}



@end
