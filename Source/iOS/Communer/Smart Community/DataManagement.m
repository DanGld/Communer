//
//  DataManagement.m
//  Smart Community
//
//  Created by oren shany on 8/9/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "DataManagement.h"
#import "Networking.h"
#import "NSDictionary+Verified.h"


@implementation DataManagement
NSMutableArray* communitiesJsonsArr;
int communitiesResponsesCount;
int numOfCommunitiesToResponse;
NSString* communityJsonObject;

+ (instancetype)sharedInstance
{
    static DataManagement *myInstance = nil;
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
        _currentCommunityID = [[NSUserDefaults standardUserDefaults] objectForKey:@"current_communityID"];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(getCommunityJsonDataResponse:)
                                                     name:@"getCommunityJsonDataResponse"
                                                   object:communityJsonObject];
    }
    return self;
}

- (void)parseJsonObjectToModels:(NSDictionary*)jsonObj {
    //parsing user object
    if ([jsonObj isKindOfClass:[NSDictionary class]]) {
    NSDictionary* userDic = jsonObj;
    _user = [[userModel alloc]init];
    _user.name = [userDic verifiedObjectForKey:@"name"] ? [userDic verifiedObjectForKey:@"name"] : @"";
     _user.lastname = [userDic verifiedObjectForKey:@"lastName"] ? [userDic verifiedObjectForKey:@"lastName"] : @"";
     _user.gender = [userDic verifiedObjectForKey:@"gender"] ? [userDic verifiedObjectForKey:@"gender"] : @"";
     _user.mStatus = [userDic verifiedObjectForKey:@"mStatus"] ? [userDic verifiedObjectForKey:@"mStatus"] : @"";
    _user.bDayInt= [userDic verifiedObjectForKey:@"bDay"] ? [[userDic verifiedObjectForKey:@"bDay"] intValue] : 0;
    
    if (_user.bDayInt!=0) {
        NSDate* userBDate = [NSDate dateWithTimeIntervalSince1970:_user.bDayInt/1000];
        NSDateFormatter *df = [[NSDateFormatter alloc] init];
        
        [df setDateFormat:@"dd"];
        _user.userBDay = [df stringFromDate:userBDate];
        
        [df setDateFormat:@"MMM"];
        _user.userBMonth = [df stringFromDate:userBDate];
        
        [df setDateFormat:@"yy"];
        _user.userBYear = [df stringFromDate:userBDate];
    }
    
    
     _user.email = [userDic verifiedObjectForKey:@"email"] ? [userDic verifiedObjectForKey:@"email"] : @"";
     _user.imageUrl = [userDic verifiedObjectForKey:@"imageURL"] ? [userDic verifiedObjectForKey:@"imageURL"] : @"";
     _user.isRabai = [[userDic verifiedObjectForKey:@"isRabai"] boolValue];    
    
    //parsing communities objects
    NSArray* communities = [userDic verifiedObjectForKey:@"communities"];
    _user.communities = [NSMutableArray array];
    
    [self parseCommunitiesArr:[userDic verifiedObjectForKey:@"communitiesUrl"]];
    _user.communities = _communitiesModels;

        }
    
}

-(void)parseJsonObjectAndAddToCurrentData:(NSDictionary*)jsonObj {

    //parsing user object
    NSDictionary* userDic = jsonObj;
    _user.name = [userDic verifiedObjectForKey:@"name"] ? [userDic verifiedObjectForKey:@"name"] : @"";
    _user.lastname = [userDic verifiedObjectForKey:@"lastName"] ? [userDic verifiedObjectForKey:@"lastName"] : @"";
    _user.gender = [userDic verifiedObjectForKey:@"gender"] ? [userDic verifiedObjectForKey:@"gender"] : @"";
    _user.mStatus = [userDic verifiedObjectForKey:@"mStatus"] ? [userDic verifiedObjectForKey:@"mStatus"] : @"";
    _user.bDayInt= [userDic verifiedObjectForKey:@"bDay"] ? [[userDic verifiedObjectForKey:@"bDay"] floatValue] : 0;
    
    if (_user.bDayInt!=0) {
        NSDate* userBDate = [NSDate dateWithTimeIntervalSince1970:_user.bDayInt/1000];
        NSDateFormatter *df = [[NSDateFormatter alloc] init];
        
        [df setDateFormat:@"dd"];
        _user.userBDay = [df stringFromDate:userBDate];
        
        [df setDateFormat:@"MMM"];
        _user.userBMonth = [df stringFromDate:userBDate];
        
        [df setDateFormat:@"yy"];
        _user.userBYear = [df stringFromDate:userBDate];
    }
    
    _user.email = [userDic verifiedObjectForKey:@"email"] ? [userDic verifiedObjectForKey:@"email"] : @"";
    _user.imageUrl = [userDic verifiedObjectForKey:@"imageURL"] ? [userDic verifiedObjectForKey:@"imageURL"] : @"";
    _user.isRabai = [[userDic verifiedObjectForKey:@"isRabai"] boolValue];
    
    //parsing communities objects
    NSArray* communities = [userDic verifiedObjectForKey:@"communities"];
    [self parseCommunitiesArr:communities];
    NSMutableArray* deltaData = _communitiesModels;
    [self mergeCurrentDataWithDeltaData:deltaData];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                        object:nil
                                                      userInfo:nil];
}

- (void)mergeCurrentDataWithDeltaData:(NSMutableArray*) deltaData {
    BOOL contains;
    
    for (CommunityModel* deltaCommunity in deltaData) {
         contains = NO;
        for (CommunityModel* existCommunity in _user.communities) {
            if ([deltaCommunity.communityID isEqualToString:existCommunity.communityID]) {
                //replace
                contains = YES;
                existCommunity.communityName = deltaCommunity.communityName;
                existCommunity.communityImageUrl = deltaCommunity.communityImageUrl;
                existCommunity.locationObj = deltaCommunity.locationObj;
                existCommunity.memberType = deltaCommunity.memberType;
                existCommunity.roofOrg = deltaCommunity.roofOrg;
                existCommunity.isSynagogue = deltaCommunity.isSynagogue;
                existCommunity.communityType = deltaCommunity.communityType;
                existCommunity.members = deltaCommunity.members;
                existCommunity.contacts = deltaCommunity.contacts;
                existCommunity.email = deltaCommunity.email;
                existCommunity.website = deltaCommunity.website;
                existCommunity.hebrewSupport = deltaCommunity.hebrewSupport;
                existCommunity.securityEvents = deltaCommunity.securityEvents;
                existCommunity.guideCategories = deltaCommunity.guideCategories;
                existCommunity.widgets = deltaCommunity.widgets;
                existCommunity.pushTags = deltaCommunity.pushTags;
                existCommunity.pushChannels = deltaCommunity.pushChannels;
                
                //merge
                
                //messages and comments
                for (EventOrMessageModel* deltaMessage in deltaCommunity.messages) {
                    BOOL containsMessage = NO;
                    for (EventOrMessageModel* existMessage in existCommunity.messages) {
                        if ([deltaMessage.eventID isEqualToString:existMessage.eventID]) {
                            containsMessage = YES;
                            for (CommentModel* deltaComment in deltaMessage.comments) {
                                BOOL containComment = NO;
                                for (CommentModel* existComment in existMessage.comments) {
                                    if ([existComment.postID isEqualToString:deltaComment.postID]) {
                                        containComment = YES;
                                    }
                                }
                                if (!containComment) {
                                    [existMessage.comments addObject:deltaComment];
                                }
                            }
                        }
                    }
                    if (!containsMessage) {
                        [existCommunity.messages addObject:deltaMessage];
                    }
                }
                
                //private QA
                for (postModel* deltaQA in deltaCommunity.privateQA) {
                    BOOL containsQA = NO;
                    for (postModel* existQA in existCommunity.privateQA) {
                        if ([deltaQA.postID isEqualToString:existQA.postID]) {
                            containsQA = YES;
                            for (CommentModel* deltaComment in deltaQA.comments) {
                                BOOL containComment = NO;
                                for (CommentModel* existComment in existQA.comments) {
                                    if ([existComment.postID isEqualToString:deltaComment.postID]) {
                                        containComment = YES;
                                    }
                                }
                                if (!containComment) {
                                    [existQA.comments addObject:deltaComment];
                                }
                            }
                        }
                    }
                    if (!containsQA) {
                        [existCommunity.privateQA addObject:deltaQA];
                    }
                }
                
                //public QA
                for (postModel* deltaQA in deltaCommunity.publicQA) {
                    BOOL containsQA = NO;
                    for (postModel* existQA in existCommunity.publicQA) {
                        if ([deltaQA.postID isEqualToString:existQA.postID]) {
                            containsQA = YES;
                            for (CommentModel* deltaComment in deltaQA.comments) {
                                BOOL containComment = NO;
                                for (CommentModel* existComment in existQA.comments) {
                                    if ([existComment.postID isEqualToString:deltaComment.postID]) {
                                        containComment = YES;
                                    }
                                }
                                if (!containComment) {
                                    [existQA.comments addObject:deltaComment];
                                }
                            }
                        }
                    }
                    if (!containsQA) {
                        [existCommunity.publicQA addObject:deltaQA];
                    }
                }
                
                //Community Posts
                for (postModel* deltaPost in deltaCommunity.communityPosts) {
                    BOOL containsPost = NO;
                    for (postModel* existPost in existCommunity.communityPosts) {
                        if ([deltaPost.postID isEqualToString:existPost.postID]) {
                            containsPost = YES;
                            for (CommentModel* deltaComment in deltaPost.comments) {
                                BOOL containComment = NO;
                                for (CommentModel* existComment in existPost.comments) {
                                    if ([existComment.postID isEqualToString:deltaComment.postID]) {
                                        containComment = YES;
                                    }
                                }
                                if (!containComment) {
                                    [existPost.comments addObject:deltaComment];
                                }
                            }
                        }
                    }
                    if (!containsPost) {
                        [existCommunity.communityPosts addObject:deltaPost];
                    }
                }
                
                //Facilities
                for (FacilityOrActivityModel* deltaFacility in deltaCommunity.facilities) {
                    BOOL containsFacility = NO;
                    for (FacilityOrActivityModel* existFacility in existCommunity.facilities) {
                        if ([deltaFacility.facilityID isEqualToString:existFacility.facilityID]) {
                            containsFacility = YES;
                            existFacility.bigImageUrl = deltaFacility.bigImageUrl;
                            existFacility.name = deltaFacility.name;
                            existFacility.descr = deltaFacility.descr;
                            existFacility.location = deltaFacility.location;
                            existFacility.images = deltaFacility.images;
                            existFacility.communityContact = deltaFacility.communityContact;
                            
                            for (EventOrMessageModel* deltaEvent in deltaFacility.events) {
                                BOOL containEvent = NO;
                                for (EventOrMessageModel* existEvent in existFacility.events) {
                                    if ([existEvent.eventID isEqualToString:deltaEvent.eventID]) {
                                        containEvent = YES;
                                        existEvent.name = deltaEvent.name;
                                        existEvent.date = existEvent.sTime;
                                        existEvent.sTime = deltaEvent.sTime;
                                        existEvent.eTime = deltaEvent.eTime;
                                        existEvent.locationObj = deltaEvent.locationObj;
                                        existEvent.content = deltaEvent.content;
                                        existEvent.imageUrl = deltaEvent.imageUrl;
                                        existEvent.readStatus = deltaEvent.readStatus;
                                        existEvent.isFree = deltaEvent.isFree;
                                        existEvent.buyLink = deltaEvent.buyLink;
                                        existEvent.attendingStatus = deltaEvent.attendingStatus;
                                        existEvent.participants = deltaEvent.participants;
                                        existEvent.type = deltaEvent.type;
                                        existEvent.isActive = deltaEvent.isActive;
                                        for (CommentModel* deltaComment in deltaEvent.comments) {
                                            BOOL containComment = NO;
                                            for (CommentModel* existComment in existEvent.comments) {
                                                if ([existComment.postID isEqualToString:deltaComment.postID]) {
                                                    containComment = YES;
                                                }
                                            }
                                            if (!containComment) {
                                                [existEvent.comments addObject:deltaComment];
                                            }
                                        }
                                        
                                    }
                                }
                                if (!containEvent) {
                                    [existFacility.events addObject:deltaEvent];
                                }
                            }
                        }
                    }
                    if (!containsFacility) {
                        [existCommunity.facilities addObject:deltaFacility];
                    }
                }
                
                //Galleries
                for (EventMediaModel* deltaGallery in deltaCommunity.eventImages) {
                    BOOL containsGallery = NO;
                    for (EventMediaModel* existGallery in existCommunity.eventImages) {
                        if ([deltaGallery.galleryID isEqualToString:existGallery.galleryID]) {
                            containsGallery = YES;
                            existGallery.imageUrl = deltaGallery.imageUrl;
                            existGallery.eventTitle = deltaGallery.eventTitle;
                            
                            for (ImageModel* deltaImage in deltaGallery.media) {
                                BOOL containImage = NO;
                                for (ImageModel* existImage in existGallery.media) {
                                    if ([existImage.imageID isEqualToString:deltaImage.imageID]) {
                                        containImage = YES;
                                    }
                                }
                                if (!containImage) {
                                    [existGallery.media addObject:deltaImage];
                                }
                            }
                        }
                    }
                    if (!containsGallery) {
                        [existCommunity.eventImages addObject:deltaGallery];
                    }
                }
                
                //Daily events
                for (dailyEventsModel* deltaDailyEvent in deltaCommunity.dailyEvents) {
                    BOOL containsDailyEvent = NO;
                    for (dailyEventsModel* existDailyEvent in existCommunity.dailyEvents) {
                        if ([deltaDailyEvent.date isEqualToString:existDailyEvent.date]) {
                            for (EventOrMessageModel* deltaEvent in deltaDailyEvent.events) {
                                BOOL containsEvent = NO;
                                for (int i=0; i<existDailyEvent.events.count;i++) {
                                    if ([((EventOrMessageModel*)existDailyEvent.events[i]).eventID isEqualToString:deltaEvent.eventID]) {
                                        existDailyEvent.events[i] = deltaEvent;
                                        containsEvent = YES;
                                    }
                                }
                                if (!containsEvent) {
                                    [existDailyEvent.events addObject:deltaEvent];
                                }
                            }
                            for (FacilityOrActivityModel* deltaActivity in deltaDailyEvent.activities) {
                                BOOL containsActivity = NO;
                                for (int i=0; i<existDailyEvent.activities.count;i++) {
                                    if ([((FacilityOrActivityModel*)existDailyEvent.activities[i]).facilityID isEqualToString:deltaActivity.facilityID]) {
                                        for (EventOrMessageModel* deltaEvent in deltaActivity.events) {
                                            BOOL containsEvent = NO;
                                            for (int z=0; z<((FacilityOrActivityModel*)existDailyEvent.activities[i]).events.count;z++) {
                                                if ([((EventOrMessageModel*)((FacilityOrActivityModel*)existDailyEvent.activities[i]).events[z]).eventID isEqualToString:deltaEvent.eventID]) {
                                                    ((FacilityOrActivityModel*)existDailyEvent.activities[i]).events[z] = deltaEvent;
                                                    containsEvent = YES;
                                                }
                                            }
                                            if (!containsEvent) {
                                                [deltaActivity.events addObject:deltaEvent];
                                            }
                                        }
                                        containsActivity = YES;
                                    }
                                }
                                if (!containsActivity) {
                                    [existDailyEvent.activities addObject:deltaActivity];
                                }
                            }
                            if (deltaDailyEvent.prayers.count>0) {
                                for (SmallPrayer* deltaPrayer in ((PrayerDetailsModel*)deltaDailyEvent.prayers[0]).prayers) {
                                    BOOL containsPrayer = NO;
                                    if (existDailyEvent.prayers.count>0) {
                                    for (int i=0; i<((PrayerDetailsModel*)existDailyEvent.prayers[0]).prayers.count;i++) {
                                        if ([((SmallPrayer*)((PrayerDetailsModel*)existDailyEvent.prayers[0]).prayers[i]).prayerID isEqualToString:deltaPrayer.prayerID]) {
                                            ((PrayerDetailsModel*)existDailyEvent.prayers[0]).prayers[i] = deltaPrayer;
                                            containsPrayer = YES;
                                        }
                                      }
                                    
                                    if (!containsPrayer) {
                                        [((PrayerDetailsModel*)existDailyEvent.prayers[0]).prayers addObject:deltaPrayer];
                                        }
                                    }
                                        else {
                                            PrayerDetailsModel* prayerDetails = [[PrayerDetailsModel alloc]init];
                                            [existDailyEvent.prayers addObject:prayerDetails];
                                            [((PrayerDetailsModel*)existDailyEvent.prayers[0]).prayers addObject:deltaPrayer];
                                        }
                                    }
                                
                                for (SmallPrayer* deltaPrayer in ((PrayerDetailsModel*)deltaDailyEvent.prayers[0]).smallPrayers) {
                                    BOOL containsPrayer = NO;
                                    if (existDailyEvent.prayers.count>0) {
                                    for (int i=0; i<((PrayerDetailsModel*)existDailyEvent.prayers[0]).smallPrayers.count;i++) {
                                        if ([((SmallPrayer*)((PrayerDetailsModel*)existDailyEvent.prayers[0]).smallPrayers[i]).prayerID isEqualToString:deltaPrayer.prayerID]) {
                                            ((PrayerDetailsModel*)existDailyEvent.prayers[0]).smallPrayers[i] = deltaPrayer;
                                            containsPrayer = YES;
                                        }
                                      }
                                    
                                    if (!containsPrayer) {
                                        [((PrayerDetailsModel*)existDailyEvent.prayers[0]).smallPrayers addObject:deltaPrayer];
                                        }
                                    }
                                    else {
                                        PrayerDetailsModel* prayerDetails = [[PrayerDetailsModel alloc]init];
                                        [existDailyEvent.prayers addObject:prayerDetails];
                                        [((PrayerDetailsModel*)existDailyEvent.prayers[0]).smallPrayers addObject:deltaPrayer];
                                    }
                                    
                                }
                        }
                        
                            containsDailyEvent = YES;
                            
                        }
                    }
                    if (!containsDailyEvent) {
                        [existCommunity.dailyEvents addObject:deltaDailyEvent];
                    }
                }
            }
           
        }
        if (!contains) {
            [_user.communities addObject:deltaCommunity]; // new community
        }
    }
}

- (void)parseJsonObjectToCommunitiesResponse:(NSArray*)jsonObj {
    //parsing communities objects
    NSArray* communities = [NSArray arrayWithArray:jsonObj];
    [self parseCommunitiesSearchResponseArr:communities];
    _communitySearchResults = _communitiesModels;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadCommunitiesSearchTable"
                                                        object:nil
                                                      userInfo:nil];
}

-(void)parseCommunitiesSearchResponseArr:(NSArray*)communities {
    _communitiesModels = [NSMutableArray array];
    for (NSDictionary* communityDic in communities) {
        CommunitySearchModel * community = [[CommunitySearchModel alloc]init];
        community.communityID = [communityDic verifiedObjectForKey:@"communityID"] ? [communityDic verifiedObjectForKey:@"communityID"] : @"";
        community.email = [communityDic verifiedObjectForKey:@"email"] ? [communityDic verifiedObjectForKey:@"email"] : @"";
        community.imageURL = [communityDic verifiedObjectForKey:@"imageURL"] ? [communityDic verifiedObjectForKey:@"imageURL"] : @"";
        NSDictionary* locationDic = [communityDic verifiedObjectForKey:@"location"] ? [communityDic verifiedObjectForKey:@"location"] : @"";
        community.location = [[LocationModel alloc]init];
        community.location.coords = [locationDic verifiedObjectForKey:@"coords"] ? [locationDic verifiedObjectForKey:@"coords"] : @"";
        community.location.title = [locationDic verifiedObjectForKey:@"title"] ? [locationDic verifiedObjectForKey:@"title"]  : @"";
        community.name = [communityDic verifiedObjectForKey:@"name"] ? [communityDic verifiedObjectForKey:@"name"] : @"";
        community.phone = [communityDic verifiedObjectForKey:@"phone"] ? [communityDic verifiedObjectForKey:@"phone"] : @"";
        community.type = [communityDic verifiedObjectForKey:@"type"] ? [communityDic verifiedObjectForKey:@"type"] : @"";
        community.website = [communityDic verifiedObjectForKey:@"website"] ? [communityDic verifiedObjectForKey:@"website"] : @"";
        community.roofOrg = [communityDic verifiedObjectForKey:@"roofOrg"] ? [communityDic verifiedObjectForKey:@"roofOrg"] : @"";
        
        [_communitiesModels addObject:community];
    }
}

-(CommunityModel*)parseToCommunityModel:(NSArray*)communities {
    [self parseCommunitiesArr:communities];
    return _communitiesModels.count>0 ? _communitiesModels[0] : [[CommunityModel alloc]init];
}

-(void)getCommunityJsonDataResponse:(NSNotification*)notification {
    [communitiesJsonsArr addObject:notification.object];
    communitiesResponsesCount++;
    if (communitiesResponsesCount==numOfCommunitiesToResponse) {
        [self parseCommunitiesArr:communitiesJsonsArr];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"getFirstTimeData"
                                                            object:nil
                                                          userInfo:nil];
    }
}


-(void)parseCommunitiesArr:(NSArray*)communities {
    _communitiesModels = [NSMutableArray array];
    
    if (communities.count>0 && [communities[0] isKindOfClass:[NSString class]]) {
    communitiesJsonsArr = [NSMutableArray array];
        communitiesResponsesCount = 0;
        numOfCommunitiesToResponse = communities.count;
    for (NSString* communityUrl in communities) {
            [[Networking sharedInstance] getCommunityDataWithUrl:communityUrl];
        }
        return;
    }
    
    for (NSDictionary* communityDic in communities) {
        CommunityModel * community = [[CommunityModel alloc]init];
        community.communityID = [communityDic verifiedObjectForKey:@"communityID"] ? [communityDic verifiedObjectForKey:@"communityID"] : @"";
        
        NSArray* messagesArr = [communityDic verifiedObjectForKey:@"announcments"];
        community.messages = [NSMutableArray array];
        for (NSDictionary* eventDic in messagesArr) {
            EventOrMessageModel* message = [[EventOrMessageModel alloc]init];
            message.eventID = [eventDic verifiedObjectForKey:@"id"] ? [eventDic verifiedObjectForKey:@"id"] : @"";
            message.name = [eventDic verifiedObjectForKey:@"name"] ? [eventDic verifiedObjectForKey:@"name"] : @"";
            NSTimeInterval dateInterval = [eventDic verifiedObjectForKey:@"date"] ? [[eventDic verifiedObjectForKey:@"date"]doubleValue] : 0;
            message.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            if (![[eventDic verifiedObjectForKey:@"sTime"] isKindOfClass:[NSNull class]] && ![[eventDic verifiedObjectForKey:@"eTime"] isKindOfClass:[NSNull class]]) {
            dateInterval = [eventDic verifiedObjectForKey:@"sTime"] ? [[eventDic verifiedObjectForKey:@"sTime"]doubleValue] : 0;
            message.sTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            dateInterval = [eventDic verifiedObjectForKey:@"eTime"] ? [[eventDic verifiedObjectForKey:@"eTime"]doubleValue] : 0;
            message.eTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            }
            
            NSDictionary* locationDic = [eventDic verifiedObjectForKey:@"location"];
            if (![locationDic isKindOfClass:[NSNull class]]) {
                LocationModel* location = [[LocationModel alloc]init];
                if ([locationDic isKindOfClass:[NSDictionary class]]) {
                    location.title = [locationDic verifiedObjectForKey:@"title"] ? [locationDic verifiedObjectForKey:@"title"] : @"";
                    location.coords = [locationDic verifiedObjectForKey:@"coords"] ? [locationDic verifiedObjectForKey:@"coords"] : @"";
                }
                message.locationObj = location;
            }
            message.content = [eventDic verifiedObjectForKey:@"content"] ? [eventDic verifiedObjectForKey:@"content"] : @"";
            message.imageUrl = [eventDic verifiedObjectForKey:@"imageURL"] ? [eventDic verifiedObjectForKey:@"imageURL"] : @"";
            message.readStatus = [eventDic verifiedObjectForKey:@"readStatus"] ? [eventDic verifiedObjectForKey:@"readStatus"] : @"";
            message.isFree = [eventDic verifiedObjectForKey:@"isFree"] ? [[eventDic verifiedObjectForKey:@"isFree"] boolValue] : true;
            message.buyLink = [eventDic verifiedObjectForKey:@"byLink"] ? [eventDic verifiedObjectForKey:@"byLink"] : @"";
            message.isActive = [eventDic verifiedObjectForKey:@"isActive"] ? [eventDic verifiedObjectForKey:@"isActive"] : @"";
            message.attendingStatus = [eventDic verifiedObjectForKey:@"attendingStatus"] ? [eventDic verifiedObjectForKey:@"attendingStatus"] : @"";
            NSArray* participants = [eventDic verifiedObjectForKey:@"participants"];
            message.participants = [NSMutableArray array];
            if ([participants isKindOfClass:[NSArray class]]) {
                for (NSDictionary* participantDic in participants) {
                    CommunityContactModel* participant = [[CommunityContactModel alloc]init];
                    participant.name = [participantDic verifiedObjectForKey:@"name"] ? [participantDic verifiedObjectForKey:@"name"] : @"";
                    participant.phoneNumber = [participantDic verifiedObjectForKey:@"phoneNumber"] ? [participantDic verifiedObjectForKey:@"phoneNumber"] : @"";
                    participant.position = [participantDic verifiedObjectForKey:@"position"] ? [participantDic verifiedObjectForKey:@"position"] : @"";
                    participant.imageUrl = [participantDic verifiedObjectForKey:@"imageUrl"] ? [participantDic verifiedObjectForKey:@"imageUrl"] : @"";
                    [message.participants addObject:participant];
                    
                };
            }
            NSArray* comments = [eventDic verifiedObjectForKey:@"comments"];
            message.comments = [NSMutableArray array];
            for (NSDictionary* commentDic in comments) {
                CommentModel* comment = [[CommentModel alloc]init];
                comment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                comment.imageUrl = [commentDic verifiedObjectForKey:@"imageUrl"] ? [commentDic verifiedObjectForKey:@"imageUrl"] : @"";
                comment.name = [commentDic verifiedObjectForKey:@"name"] ? [commentDic verifiedObjectForKey:@"name"] : @"";
                NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"date"] ? [[commentDic verifiedObjectForKey:@"date"]doubleValue] : 0;
                comment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                comment.content = [commentDic verifiedObjectForKey:@"content"] ? [commentDic verifiedObjectForKey:@"content"] : @"";
                [message.comments addObject:comment];
                
            };
            
            message.type = [eventDic verifiedObjectForKey:@"type"] ? [eventDic verifiedObjectForKey:@"type"] : @"";
            
            [community.messages addObject:message];
        }
        
        community.communityName = [communityDic verifiedObjectForKey:@"communityName"] ? [communityDic verifiedObjectForKey:@"communityName"] : @"";
        community.communityImageUrl = [communityDic verifiedObjectForKey:@"communityImageURL"] ? [communityDic verifiedObjectForKey:@"communityImageURL"] : @"";
        NSDictionary* locationDic = [communityDic verifiedObjectForKey:@"location"] ? [communityDic verifiedObjectForKey:@"location"] : @"";
        LocationModel* location = [[LocationModel alloc]init];
        if ([locationDic isKindOfClass:[NSDictionary class]]) {
        location.title = [locationDic verifiedObjectForKey:@"title"] ? [locationDic verifiedObjectForKey:@"title"] : @"";
        location.coords = [locationDic verifiedObjectForKey:@"coords"] ? [locationDic verifiedObjectForKey:@"coords"] : @"";
        }
        community.locationObj = location;
        community.roofOrg = nil; // todo
        community.isSynagogue = [[communityDic verifiedObjectForKey:@"isSynagogue"] boolValue];
        NSArray* membersArr = [communityDic verifiedObjectForKey:@"members"];
        community.members = [NSMutableArray array];
        for (NSDictionary* memberDic in membersArr) {
            CommunityMemberModel* member = [[CommunityMemberModel alloc]init];
            member.imageUrl = [memberDic verifiedObjectForKey:@"imageURL"] ? [memberDic verifiedObjectForKey:@"imageURL"] : @"";
            member.name = [memberDic verifiedObjectForKey:@"name"] ? [memberDic verifiedObjectForKey:@"name"] : @"";
            member.phone = [memberDic verifiedObjectForKey:@"phone"] ? [memberDic verifiedObjectForKey:@"phone"] : @"";
            [community.members addObject:member];
        }
        NSDictionary* contactsArr = [communityDic verifiedObjectForKey:@"contacts"] ? [communityDic verifiedObjectForKey:@"contacts"] : @"";
        if ([contactsArr isKindOfClass:[NSArray class]]) {
            community.contacts = [NSMutableArray array];
            for (NSDictionary* contactDic in contactsArr) {
                CommunityContactModel* contact = [[CommunityContactModel alloc]init];
                contact.name = [contactDic verifiedObjectForKey:@"name"] ? [contactDic verifiedObjectForKey:@"name"] : @"";
                contact.phoneNumber = [contactDic verifiedObjectForKey:@"phoneNumber"] ? [contactDic verifiedObjectForKey:@"phoneNumber"] : @"";
                contact.position = [contactDic verifiedObjectForKey:@"position"] ? [contactDic verifiedObjectForKey:@"position"] : @"";
                contact.imageUrl = [contactDic verifiedObjectForKey:@"imageURL"] ? [contactDic verifiedObjectForKey:@"imageURL"] : @"";
                [community.contacts addObject:contact];
            }
        }
        
        community.email = [communityDic verifiedObjectForKey:@"email"] ? [communityDic verifiedObjectForKey:@"email"] : @"";
        community.website = [communityDic verifiedObjectForKey:@"website"] ? [communityDic verifiedObjectForKey:@"website"] : @"";
        NSArray* privateQAArr = [communityDic verifiedObjectForKey:@"privateQA"] ? [communityDic verifiedObjectForKey:@"privateQA"] : @"";
        community.privateQA = [NSMutableArray array];
        for (NSDictionary* privateQuestion in privateQAArr) {
            
            postModel* question = [[postModel alloc]init];
            question.postID = [privateQuestion verifiedObjectForKey:@"id"] ? [privateQuestion verifiedObjectForKey:@"id"] : @"";
            
            NSDictionary* communityMemberDic = [privateQuestion verifiedObjectForKey:@"member"] ? [privateQuestion verifiedObjectForKey:@"member"] : @"";
            CommunityMemberModel* member = [[CommunityMemberModel alloc]init];
            member.name = [communityMemberDic verifiedObjectForKey:@"name"] ? [communityMemberDic verifiedObjectForKey:@"name"] : @"";
            member.imageUrl = [communityMemberDic verifiedObjectForKey:@"imageUrl"] ? [communityMemberDic verifiedObjectForKey:@"imageUrl"] : @"";
            member.phone = [communityMemberDic verifiedObjectForKey:@"phone"] ? [communityMemberDic verifiedObjectForKey:@"phone"] : @"";
            question.communityMember = member;
            
            question.title = [privateQuestion verifiedObjectForKey:@"title"] ? [privateQuestion verifiedObjectForKey:@"title"] : @"";
            question.imageUrl = [privateQuestion verifiedObjectForKey:@"imageUrl"] ? [privateQuestion verifiedObjectForKey:@"imageUrl"] : @"";
            NSTimeInterval dateInterval = [privateQuestion verifiedObjectForKey:@"date"] ? [[privateQuestion verifiedObjectForKey:@"date"]doubleValue] : 0;
            question.cDate = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            question.content = [privateQuestion verifiedObjectForKey:@"content"] ? [privateQuestion verifiedObjectForKey:@"content"] : @"";
            question.comments = [NSMutableArray array];
            NSArray* commentsArr = [privateQuestion verifiedObjectForKey:@"comments"] ? [privateQuestion verifiedObjectForKey:@"comments"] : @"";
            for (NSDictionary* commentDic in commentsArr) {
                CommentModel *tempComment = [[CommentModel alloc]init];
                NSDictionary *memberdic = [commentDic verifiedObjectForKey:@"member"];
                NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                tempComment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                tempComment.name = [memberdic verifiedObjectForKey:@"name"] ? [memberdic verifiedObjectForKey:@"name"] : @"";;
                tempComment.imageUrl = [memberdic verifiedObjectForKey:@"imageURL"] ? [memberdic verifiedObjectForKey:@"imageURL"] : @"";
                tempComment.content = [commentDic verifiedObjectForKey:@"description"] ? [commentDic verifiedObjectForKey:@"description"] : @"";
                tempComment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                tempComment.isRebai = [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue] ? [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue]  : NO;
                
                [question.comments addObject:tempComment];
            };
            
            [community.privateQA addObject:question];
            
        }
        NSArray* publicQAArr = [communityDic verifiedObjectForKey:@"publicQA"];
        community.publicQA = [NSMutableArray array];
        for (NSDictionary* publicQuestion in publicQAArr) {
            postModel* question = [[postModel alloc]init];
            question.postID = [publicQuestion verifiedObjectForKey:@"id"] ? [publicQuestion verifiedObjectForKey:@"id"] : @"";
            
            NSDictionary* communityMemberDic = [publicQuestion verifiedObjectForKey:@"member"] ? [publicQuestion verifiedObjectForKey:@"member"] : @"";
            CommunityMemberModel* member = [[CommunityMemberModel alloc]init];
            member.name = [communityMemberDic verifiedObjectForKey:@"name"] ? [communityMemberDic verifiedObjectForKey:@"name"] : @"";
            member.imageUrl = [communityMemberDic verifiedObjectForKey:@"imageUrl"] ? [communityMemberDic verifiedObjectForKey:@"imageUrl"] : @"";
            member.phone = [communityMemberDic verifiedObjectForKey:@"phone"] ? [communityMemberDic verifiedObjectForKey:@"phone"] : @"";
            question.communityMember = member;
            
            question.title = [publicQuestion verifiedObjectForKey:@"title"] ? [publicQuestion verifiedObjectForKey:@"title"] : @"";
            question.imageUrl = [publicQuestion verifiedObjectForKey:@"imageUrl"] ? [publicQuestion verifiedObjectForKey:@"imageUrl"] : @"";
            NSTimeInterval dateInterval = [publicQuestion verifiedObjectForKey:@"cd"] ? [[publicQuestion verifiedObjectForKey:@"cd"]doubleValue] : 0;
            question.cDate = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            question.content = [publicQuestion verifiedObjectForKey:@"content"] ? [publicQuestion verifiedObjectForKey:@"content"] : @"";
            question.comments = [NSMutableArray array];
            NSArray* commentsArr = [publicQuestion verifiedObjectForKey:@"comments"] ? [publicQuestion verifiedObjectForKey:@"comments"] : @"";
            for (NSDictionary* commentDic in commentsArr) {
                CommentModel *tempComment = [[CommentModel alloc]init];
                NSDictionary *memberdic = [commentDic verifiedObjectForKey:@"member"];
                NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                tempComment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                tempComment.name = [memberdic verifiedObjectForKey:@"name"] ? [memberdic verifiedObjectForKey:@"name"] : @"";;
                tempComment.imageUrl = [memberdic verifiedObjectForKey:@"imageURL"] ? [memberdic verifiedObjectForKey:@"imageURL"] : @"";
                tempComment.content = [commentDic verifiedObjectForKey:@"description"] ? [commentDic verifiedObjectForKey:@"description"] : @"";
                tempComment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                tempComment.isRebai = [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue] ? [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue]  : NO;
                
                [question.comments addObject:tempComment];
            };
            
            [community.publicQA addObject:question];
        }
        NSArray* communityPostsArr = [communityDic verifiedObjectForKey:@"communityPosts"];
        community.communityPosts = [NSMutableArray array];
        for (NSDictionary* communityPost in communityPostsArr) {
            postModel* post = [[postModel alloc]init];
            post.postID = [communityPost verifiedObjectForKey:@"id"] ? [communityPost verifiedObjectForKey:@"id"] : @"";
            
            NSDictionary* communityMemberDic = [communityPost verifiedObjectForKey:@"member"] ? [communityPost verifiedObjectForKey:@"member"] : @"";
            CommunityMemberModel* member = [[CommunityMemberModel alloc]init];
            member.name = [communityMemberDic verifiedObjectForKey:@"name"] ? [communityMemberDic verifiedObjectForKey:@"name"] : @"";
            member.imageUrl = [communityMemberDic verifiedObjectForKey:@"imageUrl"] ? [communityMemberDic verifiedObjectForKey:@"imageUrl"] : @"";
            member.phone = [communityMemberDic verifiedObjectForKey:@"phone"] ? [communityMemberDic verifiedObjectForKey:@"phone"] : @"";
            post.communityMember = member;
            
            post.title = [communityPost verifiedObjectForKey:@"title"] ? [communityPost verifiedObjectForKey:@"title"] : @"";
            post.imageUrl = [communityPost verifiedObjectForKey:@"imageUrl"] ? [communityPost verifiedObjectForKey:@"imageUrl"] : @"";
            NSTimeInterval dateInterval = [communityPost verifiedObjectForKey:@"cd"] ? [[communityPost verifiedObjectForKey:@"cd"]doubleValue] : 0;
            post.cDate = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            post.content = [communityPost verifiedObjectForKey:@"content"] ? [communityPost verifiedObjectForKey:@"content"] : @"";
            post.comments = [NSMutableArray array];
            NSArray* commentsArr = [communityPost verifiedObjectForKey:@"comments"] ? [communityPost verifiedObjectForKey:@"comments"] : @"";
            for (NSDictionary* commentDic in commentsArr) {
                CommentModel *tempComment = [[CommentModel alloc]init];
                NSDictionary *memberdic = [commentDic verifiedObjectForKey:@"member"];
                NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                tempComment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                if ([memberdic isKindOfClass:[NSDictionary class]]) {
                    tempComment.name = [memberdic verifiedObjectForKey:@"name"] ? [memberdic verifiedObjectForKey:@"name"] : @"";;
                    tempComment.imageUrl = [memberdic verifiedObjectForKey:@"imageURL"] ? [memberdic verifiedObjectForKey:@"imageURL"] : @"";
                }
                tempComment.content = [commentDic verifiedObjectForKey:@"description"] ? [commentDic verifiedObjectForKey:@"description"] : @"";
                tempComment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                tempComment.isRebai = [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue] ? [[commentDic verifiedObjectForKey:@"isFromRav"]boolValue]  : NO;
                
                [post.comments addObject:tempComment];
            };
            
            [community.communityPosts addObject:post];
        }
        community.hebrewSupport = [[communityDic verifiedObjectForKey:@"hebrewSupport"] boolValue];
        NSArray* facilitiesArr = [communityDic verifiedObjectForKey:@"facilities"];
        community.facilities = [NSMutableArray array];
        for (NSDictionary* facilityDic in facilitiesArr) {
            FacilityOrActivityModel* facility = [[FacilityOrActivityModel alloc]init];
            facility.facilityID = [facilityDic verifiedObjectForKey:@"id"] ? [facilityDic verifiedObjectForKey:@"id"] : @"";
            facility.bigImageUrl = [facilityDic verifiedObjectForKey:@"bigImageURL"] ? [facilityDic verifiedObjectForKey:@"bigImageURL"] : @"";
            facility.name = [facilityDic verifiedObjectForKey:@"name"] ? [facilityDic verifiedObjectForKey:@"name"] : @"";
            facility.descr = [facilityDic verifiedObjectForKey:@"description"] ? [facilityDic verifiedObjectForKey:@"description"] : @"";
            facility.images = [facilityDic verifiedObjectForKey:@"images"] ? [facilityDic verifiedObjectForKey:@"images"] : @"";
            NSDictionary* locationDic = [facilityDic verifiedObjectForKey:@"location"] ? [facilityDic verifiedObjectForKey:@"location"] : @"";
            facility.location = [[LocationModel alloc]init];
            facility.location.coords = [locationDic verifiedObjectForKey:@"coords"];
            facility.location.title = [locationDic verifiedObjectForKey:@"title"];
            facility.events = [NSMutableArray array];
            NSArray* eventsArr = [facilityDic verifiedObjectForKey:@"events"] ? [facilityDic verifiedObjectForKey:@"events"] : [NSArray array];
            for (NSDictionary* eventDic in eventsArr) {
                EventOrMessageModel* event = [[EventOrMessageModel alloc]init];
                event.eventID = [eventDic verifiedObjectForKey:@"id"] ? [eventDic verifiedObjectForKey:@"id"] : @"";
                event.name = [eventDic verifiedObjectForKey:@"name"] ? [eventDic verifiedObjectForKey:@"name"] : @"";
                NSTimeInterval dateInterval = [eventDic verifiedObjectForKey:@"date"] ? [[eventDic verifiedObjectForKey:@"date"]doubleValue] : 0;
                dateInterval = [eventDic verifiedObjectForKey:@"sTime"] ? [[eventDic verifiedObjectForKey:@"sTime"]doubleValue] : 0;
                event.date = dateInterval;
                event.sTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                dateInterval = [eventDic verifiedObjectForKey:@"eTime"] ? [[eventDic verifiedObjectForKey:@"eTime"]doubleValue] : 0;
                event.eTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                NSDictionary* locationDic = [eventDic verifiedObjectForKey:@"location"];
                if ([locationDic isKindOfClass:[NSDictionary class]]) {
                LocationModel* location = [[LocationModel alloc]init];
                location.title = [locationDic verifiedObjectForKey:@"title"] ? [locationDic verifiedObjectForKey:@"title"] : @"";
                location.coords = [locationDic verifiedObjectForKey:@"coords"] ? [locationDic verifiedObjectForKey:@"coords"] : @"";
                event.locationObj = location;
                }
                event.content = [eventDic verifiedObjectForKey:@"content"] ? [eventDic verifiedObjectForKey:@"content"] : @"";
                event.imageUrl = [eventDic verifiedObjectForKey:@"imageURL"] ? [eventDic verifiedObjectForKey:@"imageURL"] : @"";
                event.readStatus = [eventDic verifiedObjectForKey:@"readStatus"] ? [eventDic verifiedObjectForKey:@"readStatus"] : @"";
                event.isFree = [eventDic verifiedObjectForKey:@"isFree"] ? [eventDic verifiedObjectForKey:@"isFree"] : @"";
                event.buyLink = [eventDic verifiedObjectForKey:@"byLink"] ? [eventDic verifiedObjectForKey:@"byLink"] : @"";
                event.isActive = [eventDic verifiedObjectForKey:@"isActive"] ? [eventDic verifiedObjectForKey:@"isActive"] : @"";
                event.attendingStatus = [eventDic verifiedObjectForKey:@"attendingStatus"] ? [eventDic verifiedObjectForKey:@"attendingStatus"] : @"";
                NSArray* participants = [eventDic verifiedObjectForKey:@"participants"];
                event.participants = [NSMutableArray array];
                for (NSDictionary* participantDic in participants) {
                    CommunityContactModel* participant = [[CommunityContactModel alloc]init];
                    participant.name = [participantDic verifiedObjectForKey:@"name"] ? [participantDic verifiedObjectForKey:@"name"] : @"";
                    participant.phoneNumber = [participantDic verifiedObjectForKey:@"phoneNumber"] ? [participantDic verifiedObjectForKey:@"phoneNumber"] : @"";
                    participant.position = [participantDic verifiedObjectForKey:@"position"] ? [participantDic verifiedObjectForKey:@"position"] : @"";
                    participant.imageUrl = [participantDic verifiedObjectForKey:@"imageUrl"] ? [participantDic verifiedObjectForKey:@"imageUrl"] : @"";
                    [event.participants addObject:participant];
                    
                };
                NSArray* comments = [eventDic verifiedObjectForKey:@"comments"];
                event.comments = [NSMutableArray array];
                for (NSDictionary* commentDic in comments) {
                    CommentModel* comment = [[CommentModel alloc]init];
                    comment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                    comment.imageUrl = [commentDic verifiedObjectForKey:@"imageUrl"] ? [commentDic verifiedObjectForKey:@"imageUrl"] : @"";
                    comment.name = [commentDic verifiedObjectForKey:@"name"] ? [commentDic verifiedObjectForKey:@"name"] : @"";
                    NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                    comment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                    comment.content = [commentDic verifiedObjectForKey:@"content"] ? [commentDic verifiedObjectForKey:@"content"] : @"";
                    [event.comments addObject:comment];
                    
                }
                
                if (event.isActive) {
                    [facility.events addObject:event];
                }

            }
            NSArray* contactsArr = [facilityDic verifiedObjectForKey:@"contact"] ? [facilityDic verifiedObjectForKey:@"contact"] : @"";
            if (contactsArr.count>0) {
                NSDictionary* contactDic = contactsArr[0];
            CommunityContactModel* contact = [[CommunityContactModel alloc]init];
            contact.name = [contactDic verifiedObjectForKey:@"name"] ? [contactDic verifiedObjectForKey:@"name"] : @"";
            contact.position = [contactDic verifiedObjectForKey:@"position"] ? [contactDic verifiedObjectForKey:@"position"] : @"";
            contact.phoneNumber = [contactDic verifiedObjectForKey:@"phoneNumber"] ? [contactDic verifiedObjectForKey:@"phoneNumber"] : @"";
            contact.imageUrl = [contactDic verifiedObjectForKey:@"imageURL"] ? [contactDic verifiedObjectForKey:@"imageURL"] : @"";
            facility.communityContact = contact;
            }
            
            [community.facilities addObject:facility];
        }
        
        NSArray* dailyEventsArr = [communityDic verifiedObjectForKey:@"daylyEvents"];
        community.dailyEvents = [NSMutableArray array];
        for (NSDictionary* dailyEventDic in dailyEventsArr) {
            dailyEventsModel* dailyEvents = [[dailyEventsModel alloc]init];
            
            NSString* dateString = [dailyEventDic verifiedObjectForKey:@"date"] ? [dailyEventDic verifiedObjectForKey:@"date"] : 0;
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"dd/MM/yyyy HH:mm:ss"];
            dailyEvents.date = dateString;//[dateFormatter dateFromString: [NSString stringWithFormat:@"%@ 10:00:00",dateString]];
            NSArray* eventsArr = [dailyEventDic verifiedObjectForKey:@"events"];
            dailyEvents.events = [NSMutableArray array];
            for (NSDictionary* eventDic in eventsArr) {
                EventOrMessageModel* event = [[EventOrMessageModel alloc]init];
                event.eventID = [eventDic verifiedObjectForKey:@"id"] ? [eventDic verifiedObjectForKey:@"id"] : @"";
                event.name = [eventDic verifiedObjectForKey:@"name"] ? [eventDic verifiedObjectForKey:@"name"] : @"";
                NSTimeInterval dateInterval = [eventDic verifiedObjectForKey:@"date"] ? [[eventDic verifiedObjectForKey:@"date"]doubleValue] : 0;
                 dateInterval = [eventDic verifiedObjectForKey:@"sTime"] ? [[eventDic verifiedObjectForKey:@"sTime"]doubleValue] : 0;
                event.date = dateInterval;
                event.sTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                dateInterval = [eventDic verifiedObjectForKey:@"eTime"] ? [[eventDic verifiedObjectForKey:@"eTime"]doubleValue] : 0;
                event.eTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                NSDictionary* locationDic = [eventDic verifiedObjectForKey:@"location"];
                LocationModel* location = [[LocationModel alloc]init];
                location.title = [locationDic verifiedObjectForKey:@"title"] ? [locationDic verifiedObjectForKey:@"title"] : @"";
                location.coords = [locationDic verifiedObjectForKey:@"coords"] ? [locationDic verifiedObjectForKey:@"coords"] : @"";
                event.locationObj = location;
                event.content = [eventDic verifiedObjectForKey:@"content"] ? [eventDic verifiedObjectForKey:@"content"] : @"";
                event.imageUrl = [eventDic verifiedObjectForKey:@"imageURL"] ? [eventDic verifiedObjectForKey:@"imageURL"] : @"";
                event.readStatus = [eventDic verifiedObjectForKey:@"readStatus"] ? [eventDic verifiedObjectForKey:@"readStatus"] : @"";
                event.isFree = [eventDic verifiedObjectForKey:@"isFree"] ? [eventDic verifiedObjectForKey:@"isFree"] : @"";
                event.buyLink = [eventDic verifiedObjectForKey:@"byLink"] ? [eventDic verifiedObjectForKey:@"byLink"] : @"";
                event.isActive = [eventDic verifiedObjectForKey:@"isActive"] ? [eventDic verifiedObjectForKey:@"isActive"] : @"";
                event.attendingStatus = [eventDic verifiedObjectForKey:@"attendingStatus"] ? [eventDic verifiedObjectForKey:@"attendingStatus"] : @"";
                NSArray* participants = [eventDic verifiedObjectForKey:@"participants"];
                event.participants = [NSMutableArray array];
                for (NSDictionary* participantDic in participants) {
                    CommunityContactModel* participant = [[CommunityContactModel alloc]init];
                    participant.name = [participantDic verifiedObjectForKey:@"name"] ? [participantDic verifiedObjectForKey:@"name"] : @"";
                    participant.phoneNumber = [participantDic verifiedObjectForKey:@"phoneNumber"] ? [participantDic verifiedObjectForKey:@"phoneNumber"] : @"";
                    participant.position = [participantDic verifiedObjectForKey:@"position"] ? [participantDic verifiedObjectForKey:@"position"] : @"";
                    participant.imageUrl = [participantDic verifiedObjectForKey:@"imageUrl"] ? [participantDic verifiedObjectForKey:@"imageUrl"] : @"";
                    [event.participants addObject:participant];
                    
                };
                NSArray* comments = [eventDic verifiedObjectForKey:@"comments"];
                event.comments = [NSMutableArray array];
                for (NSDictionary* commentDic in comments) {
                    CommentModel* comment = [[CommentModel alloc]init];
                    comment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                    comment.imageUrl = [commentDic verifiedObjectForKey:@"imageUrl"] ? [commentDic verifiedObjectForKey:@"imageUrl"] : @"";
                    comment.name = [commentDic verifiedObjectForKey:@"name"] ? [commentDic verifiedObjectForKey:@"name"] : @"";
                    NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                    comment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                    comment.content = [commentDic verifiedObjectForKey:@"content"] ? [commentDic verifiedObjectForKey:@"content"] : @"";
                    [event.comments addObject:comment];
                    
                };
                
                if (event.isActive) {
                [dailyEvents.events addObject:event];
                }
            }
            
            
            NSArray* activitiesArr = [dailyEventDic verifiedObjectForKey:@"activities"];
            NSMutableArray* activities = [NSMutableArray array];
            for (NSDictionary* activityDic in activitiesArr) {
                FacilityOrActivityModel* activity = [[FacilityOrActivityModel alloc]init];
                activity.facilityID = [activityDic verifiedObjectForKey:@"id"] ? [activityDic verifiedObjectForKey:@"id"] : @"";
                activity.bigImageUrl = [activityDic verifiedObjectForKey:@"bigImageURL"] ? [activityDic verifiedObjectForKey:@"bigImageURL"] : @"";
                activity.name = [activityDic verifiedObjectForKey:@"name"] ? [activityDic verifiedObjectForKey:@"name"] : @"";
                activity.descr = [activityDic verifiedObjectForKey:@"description"] ? [activityDic verifiedObjectForKey:@"description"] : @"";
                activity.images = [activityDic verifiedObjectForKey:@"images"] ? [activityDic verifiedObjectForKey:@"images"] : @"";
                activity.location = [activityDic verifiedObjectForKey:@"location"] ? [activityDic verifiedObjectForKey:@"location"] : @"";
                NSArray * activityEventsArr = [activityDic verifiedObjectForKey:@"events"] ? [activityDic verifiedObjectForKey:@"events"] : @"";
                activity.events = [NSMutableArray array];
                for (NSDictionary* eventDic in activityEventsArr) {
                    EventOrMessageModel* event = [[EventOrMessageModel alloc]init];
                    event.eventID = [eventDic verifiedObjectForKey:@"id"] ? [eventDic verifiedObjectForKey:@"id"] : @"";
                    event.name = [eventDic verifiedObjectForKey:@"name"] ? [eventDic verifiedObjectForKey:@"name"] : @"";
                    event.type = @"activity";
                    NSTimeInterval dateInterval = [eventDic verifiedObjectForKey:@"date"] ? [[eventDic verifiedObjectForKey:@"date"]doubleValue] : 0;
                    dateInterval = [eventDic verifiedObjectForKey:@"sTime"] ? [[eventDic verifiedObjectForKey:@"sTime"]doubleValue] : 0;
                    event.date = dateInterval;
                    event.sTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                    dateInterval = [eventDic verifiedObjectForKey:@"eTime"] ? [[eventDic verifiedObjectForKey:@"eTime"]doubleValue] : 0;
                    event.eTime = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                    event.locationObj = location;
                    event.content = [eventDic verifiedObjectForKey:@"content"] ? [eventDic verifiedObjectForKey:@"content"] : @"";
                    event.imageUrl = [eventDic verifiedObjectForKey:@"imageURL"] ? [eventDic verifiedObjectForKey:@"imageURL"] : @"";
                    event.readStatus = [eventDic verifiedObjectForKey:@"readStatus"] ? [eventDic verifiedObjectForKey:@"readStatus"] : @"";
                    event.isFree = [eventDic verifiedObjectForKey:@"isFree"] ? [eventDic verifiedObjectForKey:@"isFree"] : @"";
                    event.buyLink = [eventDic verifiedObjectForKey:@"byLink"] ? [eventDic verifiedObjectForKey:@"byLink"] : @"";
                    event.isActive = [eventDic verifiedObjectForKey:@"isActive"] ? [eventDic verifiedObjectForKey:@"isActive"] : @"";
                    event.attendingStatus = [eventDic verifiedObjectForKey:@"attendingStatus"] ? [eventDic verifiedObjectForKey:@"attendingStatus"] : @"";
                    NSArray* participants = [eventDic verifiedObjectForKey:@"participants"];
                    event.participants = [NSMutableArray array];
                    if ([participants isKindOfClass:[NSArray class]]) {
                        for (NSDictionary* participantDic in participants) {
                            CommunityContactModel* participant = [[CommunityContactModel alloc]init];
                            participant.name = [participantDic verifiedObjectForKey:@"name"] ? [participantDic verifiedObjectForKey:@"name"] : @"";
                            participant.phoneNumber = [participantDic verifiedObjectForKey:@"phoneNumber"] ? [participantDic verifiedObjectForKey:@"phoneNumber"] : @"";
                            participant.position = [participantDic verifiedObjectForKey:@"position"] ? [participantDic verifiedObjectForKey:@"position"] : @"";
                            participant.imageUrl = [participantDic verifiedObjectForKey:@"imageUrl"] ? [participantDic verifiedObjectForKey:@"imageUrl"] : @"";
                            [event.participants addObject:participant];
                            
                        }
                    };
                    NSArray* comments = [eventDic verifiedObjectForKey:@"comments"];
                    event.comments = [NSMutableArray array];
                    if ([comments isKindOfClass:[NSArray class]]) {
                        for (NSDictionary* commentDic in comments) {
                            CommentModel* comment = [[CommentModel alloc]init];
                            comment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
                            comment.imageUrl = [commentDic verifiedObjectForKey:@"imageUrl"] ? [commentDic verifiedObjectForKey:@"imageUrl"] : @"";
                            comment.name = [commentDic verifiedObjectForKey:@"name"] ? [commentDic verifiedObjectForKey:@"name"] : @"";
                            NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"cd"] ? [[commentDic verifiedObjectForKey:@"cd"]doubleValue] : 0;
                            comment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
                            comment.content = [commentDic verifiedObjectForKey:@"content"] ? [commentDic verifiedObjectForKey:@"content"] : @"";
                            [event.comments addObject:comment];
                            
                        }
                    }
                    event.name = activity.name;
                    [activity.events addObject:event];
                }
                
                
                
                
                activity.communityContact = [activityDic verifiedObjectForKey:@"contact"] ? [activityDic verifiedObjectForKey:@"contact"] : @"";
                [activities addObject:activity];
            }
            dailyEvents.activities = activities;
            dailyEvents.prayers = [NSMutableArray array];
            NSMutableArray* prayersDetailsArr = [dailyEventDic verifiedObjectForKey:@"prayers"];
            for (NSDictionary* prayersDetailsDic in prayersDetailsArr) {
                PrayerDetailsModel* prayer = [[PrayerDetailsModel alloc]init];
                prayer.communityID = [prayersDetailsDic verifiedObjectForKey:@"communityID"] ? [prayersDetailsDic verifiedObjectForKey:@"communityID"] : @"";
                NSTimeInterval dateInterval = [prayersDetailsDic verifiedObjectForKey:@"date"] ? [[prayersDetailsDic verifiedObjectForKey:@"date"]doubleValue] : 0;
                prayer.date = dateInterval;
                prayer.parashaName = [prayersDetailsDic verifiedObjectForKey:@"parasha"] ? [prayersDetailsDic verifiedObjectForKey:@"parasha"] : @"";
                NSDictionary* prayersDic = [prayersDetailsDic verifiedObjectForKey:@"prayers"] ? [prayersDetailsDic verifiedObjectForKey:@"prayers"] : @"";
                NSArray* timesArr = [prayersDic verifiedObjectForKey:@"times"] ? [prayersDic verifiedObjectForKey:@"times"] : @"";
                prayer.prayers = [NSMutableArray array];
                if ([timesArr isKindOfClass:[NSArray class]]) {
                    for (NSDictionary* smallPrayerDic in timesArr) {
                        SmallPrayer* smallPrayer = [[SmallPrayer alloc]init];
                        smallPrayer.prayerID = [smallPrayerDic verifiedObjectForKey:@"id"] ? [smallPrayerDic verifiedObjectForKey:@"id"] : @"";
                        smallPrayer.time = [smallPrayerDic verifiedObjectForKey:@"time"] ? [smallPrayerDic verifiedObjectForKey:@"time"] : @"";
                        smallPrayer.title = [smallPrayerDic verifiedObjectForKey:@"title"] ? [smallPrayerDic verifiedObjectForKey:@"title"] : @"";
                        smallPrayer.type = [smallPrayerDic verifiedObjectForKey:@"type"] ? [smallPrayerDic verifiedObjectForKey:@"type"] : @"";
                        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                        [dateFormatter setDateFormat:@"dd/MM/yyyy"];
                        NSString* dateString = [dateFormatter stringFromDate: [NSDate dateWithTimeIntervalSince1970:prayer.date/1000]];
                        [dateFormatter setDateFormat:@"dd/MM/yyyy HH:mm"];
                        NSDate* prayerDate = [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ %@", dateString, smallPrayer.time]];
                        
                        smallPrayer.date = [prayerDate timeIntervalSince1970]*1000;
                        [prayer.prayers addObject:smallPrayer];
                    }
                }
                prayer.holidayStart = [prayersDetailsDic verifiedObjectForKey:@"holidayStart"] ? [prayersDetailsDic verifiedObjectForKey:@"holidayStart"] : @"";
                prayer.holidayEnd = [prayersDetailsDic verifiedObjectForKey:@"holidayEnd"] ? [prayersDetailsDic verifiedObjectForKey:@"holidayEnd"] : @"";
                NSArray* smallPrayersArr = [prayersDetailsDic verifiedObjectForKey:@"smallPrayers"] ? [prayersDetailsDic verifiedObjectForKey:@"smallPrayers"] : @"";
                prayer.smallPrayers = [NSMutableArray array];
                if ([smallPrayersArr isKindOfClass:[NSArray class]]) {
                for (NSDictionary* smallPrayerDic in smallPrayersArr) {
                    SmallPrayer* smallPrayer = [[SmallPrayer alloc]init];
                    smallPrayer.prayerID = [smallPrayerDic verifiedObjectForKey:@"id"] ? [smallPrayerDic verifiedObjectForKey:@"id"] : @"";
                    smallPrayer.time = [smallPrayerDic verifiedObjectForKey:@"time"] ? [smallPrayerDic verifiedObjectForKey:@"time"] : @"";
                    smallPrayer.title = [smallPrayerDic verifiedObjectForKey:@"title"] ? [smallPrayerDic verifiedObjectForKey:@"title"] : @"";
                    smallPrayer.type = [smallPrayerDic verifiedObjectForKey:@"type"] ? [smallPrayerDic verifiedObjectForKey:@"type"] : @"";
                    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                    [dateFormatter setDateFormat:@"dd/MM/yyyy"];
                    NSString* dateString = [dateFormatter stringFromDate: [NSDate dateWithTimeIntervalSince1970:prayer.date/1000]];
                    [dateFormatter setDateFormat:@"dd/MM/yyyy HH:mm"];
                    NSDate* prayerDate = [dateFormatter dateFromString:[NSString stringWithFormat:@"%@ %@", dateString, smallPrayer.time]];
                    
                    smallPrayer.date = [prayerDate timeIntervalSince1970]*1000;
                    [prayer.smallPrayers addObject:smallPrayer];
                    }
                }
                
                NSArray* additionsArray = [prayersDetailsDic verifiedObjectForKey:@"additions"] ? [prayersDetailsDic verifiedObjectForKey:@"additions"] : @"";
                prayer.additions = [NSMutableArray array];
                if ([additionsArray isKindOfClass:[NSArray class]]) {
                    prayer.additions = additionsArray;
                }

                
                [dailyEvents.prayers addObject:prayer];
            }
            [community.dailyEvents addObject:dailyEvents];
        }
        
        community.eventImages = [NSMutableArray array];
        NSArray* eventsImg = [communityDic verifiedObjectForKey:@"eventsImages"];
        for (NSDictionary* eventMediaDic in eventsImg) {
            EventMediaModel* eventMedia = [[EventMediaModel alloc]init];
            eventMedia.galleryID = [eventMediaDic verifiedObjectForKey:@"galleryID"];
            eventMedia.eventTitle = [eventMediaDic verifiedObjectForKey:@"eventTitle"];
            NSArray* imagesArr = [eventMediaDic verifiedObjectForKey:@"images"];
            eventMedia.media = [NSMutableArray array];
            for (NSDictionary* imagesDic in imagesArr) {
                ImageModel* image = [[ImageModel alloc]init];
                image.imageID = [imagesDic verifiedObjectForKey:@"id"];
                image.link = [imagesDic verifiedObjectForKey:@"link"];
                [eventMedia.media addObject:image];
            }
            eventMedia.imageUrl = [eventMediaDic verifiedObjectForKey:@"galleryImg"];
            
            [community.eventImages addObject:eventMedia];
        }
        community.guideCategories = [NSMutableArray array];
        NSArray* guideCategoriesArr = [communityDic verifiedObjectForKey:@"guideCategories"];
        for (NSDictionary* guideCategoryDic in guideCategoriesArr) {
            GuideCategoryModel* guideCategory = [[GuideCategoryModel alloc]init];
            guideCategory.title = [guideCategoryDic verifiedObjectForKey:@"title"] ? [guideCategoryDic verifiedObjectForKey:@"title"] : @"";
            guideCategory.imageUrl = [guideCategoryDic verifiedObjectForKey:@"imageURL"] ? [guideCategoryDic verifiedObjectForKey:@"imageURL"] : @"";
            [community.guideCategories addObject:guideCategory];
        }
        
        community.securityEvents = [NSMutableArray array];
        NSArray* securityEventsArr = [communityDic verifiedObjectForKey:@"securityEvents"];
        for (NSDictionary* securityEventDic in securityEventsArr) {
            SecurityEventModel* securityEvent = [[SecurityEventModel alloc]init];
            securityEvent.reporterName = [securityEventDic verifiedObjectForKey:@"reporterName"] ? [securityEventDic verifiedObjectForKey:@"reporterName"] : @"";
            securityEvent.report = [[ReportModel alloc]init];
            NSDictionary* reportDic = [securityEventDic verifiedObjectForKey:@"report"];
            securityEvent.report.location = [[LocationModel alloc]init];
            NSDictionary* locationDic = [reportDic verifiedObjectForKey:@"location"] ? [reportDic verifiedObjectForKey:@"location"] : @"";
            if ([locationDic isKindOfClass:[NSDictionary class]] && ![[locationDic verifiedObjectForKey:@"coords"] isKindOfClass:[NSNull class]] && ![[locationDic verifiedObjectForKey:@"coords"] isEqualToString:@""]) {
                securityEvent.report.location.title = [locationDic verifiedObjectForKey:@"title"];
                securityEvent.report.location.coords = [locationDic verifiedObjectForKey:@"coords"];
            }
            securityEvent.report.type = [reportDic verifiedObjectForKey:@"type"] ? [reportDic verifiedObjectForKey:@"type"] : @"";
            securityEvent.report.desc = [reportDic verifiedObjectForKey:@"description"] ? [reportDic verifiedObjectForKey:@"description"] : @"";
            securityEvent.report.isPoliceReport = [reportDic verifiedObjectForKey:@"isPoliceReport"] ? [reportDic verifiedObjectForKey:@"isPoliceReport"] : @"";
            securityEvent.report.isAnnonimus = [reportDic verifiedObjectForKey:@"isAnnonimus"] ? [reportDic verifiedObjectForKey:@"isAnnonimus"] : @"";
            securityEvent.report.severity = [reportDic verifiedObjectForKey:@"severity"] ? [reportDic verifiedObjectForKey:@"severity"] : @"";
            
            [community.securityEvents addObject:securityEvent];
        }
        
        community.memberType = [communityDic verifiedObjectForKey:@"memberType"];
        community.visibleModules = [NSMutableArray arrayWithArray:[communityDic verifiedObjectForKey:@"visibleModules"]];
        community.widgets = [NSMutableArray arrayWithArray:[communityDic verifiedObjectForKey:@"widgets"]];
        
         [_communitiesModels addObject:community];
    }
}

- (NSMutableArray*)parseJsonObjectToGuidesList:(NSDictionary*)jsonObj{
    NSMutableArray* guidesList = [NSMutableArray array];
    if ([jsonObj verifiedObjectForKey:@"results"]) {
    for (NSDictionary* contact in [jsonObj verifiedObjectForKey:@"results"]) {
        ServiceContactModel* serviceContact = [[ServiceContactModel alloc]init];
        serviceContact.imageUrl = [contact verifiedObjectForKey:@"imageURL"] ? [contact verifiedObjectForKey:@"imageURL"] : @"";
        serviceContact.name = [contact verifiedObjectForKey:@"name"] ? [contact verifiedObjectForKey:@"name"] : @"";
        LocationModel* location = [[LocationModel alloc]init];
        NSDictionary* locationDic = [contact verifiedObjectForKey:@"location"] ? [contact verifiedObjectForKey:@"location"] : @"";
        if (locationDic) {
            location.title = [locationDic verifiedObjectForKey:@"title"];
            location.coords = [locationDic verifiedObjectForKey:@"coords"];
        }
        serviceContact.location = location;
        serviceContact.phoneNumber = [contact verifiedObjectForKey:@"phone"] ? [contact verifiedObjectForKey:@"phone"] : @"";
        serviceContact.rating = [contact verifiedObjectForKey:@"rating"] ? [[contact verifiedObjectForKey:@"rating"]doubleValue] : -1;
        serviceContact.comments = [NSMutableArray array];
        NSMutableArray* commentsArr = [contact verifiedObjectForKey:@"comments"] ? [contact verifiedObjectForKey:@"comments"] : [NSArray array];
        for (NSDictionary* commentDic in commentsArr) {
            CommentModel* comment = [[CommentModel alloc]init];
            comment.postID = [commentDic verifiedObjectForKey:@"postID"] ? [commentDic verifiedObjectForKey:@"postID"] : @"";
            comment.imageUrl = [commentDic verifiedObjectForKey:@"imageUrl"] ? [commentDic verifiedObjectForKey:@"imageUrl"] : @"";
            comment.name = [commentDic verifiedObjectForKey:@"name"] ? [commentDic verifiedObjectForKey:@"name"] : @"";
            NSTimeInterval dateInterval = [commentDic verifiedObjectForKey:@"date"] ? [[commentDic verifiedObjectForKey:@"date"]doubleValue] : 0;
            comment.date = dateInterval;//[[NSDate alloc]initWithTimeIntervalSince1970:dateInterval/1000];
            comment.content = [commentDic verifiedObjectForKey:@"content"] ? [commentDic verifiedObjectForKey:@"content"] : @"";
            [serviceContact.comments addObject:comment];
        };
        
        serviceContact.serviceContactID = [contact verifiedObjectForKey:@"id"] ? [contact verifiedObjectForKey:@"id"] : @"";

        [guidesList addObject:serviceContact];
    }
    }
    
    return guidesList;
}

-(void)removeCommunityWithCommunityID:(NSString*)communityID {
    for (int i=0; i<_user.communities.count; i++) {
        if ([((CommunityModel*)_user.communities[i]).communityID isEqualToString:communityID]) {
            [_user.communities removeObjectAtIndex:i];
            _currentCommunityID = ((CommunityModel*)_user.communities[0]).communityID;
            [[NSUserDefaults standardUserDefaults] setObject:_currentCommunityID forKey:@"current_communityID"];
            NSDictionary* jsonString = [[DataManagement sharedInstance].user objectToJson];
            [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
        }
    }
}

@end
