//
//  DataManagement.h
//  Smart Community
//
//  Created by oren shany on 8/9/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "userModel.h"
#import "CommunityModel.h"
#import "CommunitySearchModel.h"
#import "EventOrMessageModel.h"
#import "CommunityContactModel.h"
#import "CommentModel.h"
#import "CommunityMemberModel.h"
#import "postModel.h"
#import "FacilityOrActivityModel.h"
#import "PrayerDetailsModel.h"
#import "SmallPrayer.h"
#import "EventMediaModel.h"
#import "dailyEventsModel.h"
#import "GuideCategoryModel.h"
#import "ServiceContactModel.h"
#import "ImageModel.h"
#import "SecurityEventModel.h"
#import "ReportModel.h"

@interface DataManagement : NSObject

@property (nonatomic, strong) userModel* user;//5156544
@property (nonatomic, strong) NSString* currentCommunityID;
@property (nonatomic, strong) NSString* lastCurrentCommunityID;
@property (nonatomic, strong) NSMutableArray* communitySearchResults;
@property (nonatomic, strong) NSMutableArray* communitiesModels;

+ (instancetype)sharedInstance;

- (void)parseJsonObjectToModels:(NSDictionary*)jsonObj;
-(void)parseJsonObjectAndAddToCurrentData:(NSDictionary*)jsonObj;
- (void)parseJsonObjectToCommunitiesResponse:(NSDictionary*)jsonObj;
- (NSMutableArray*)parseJsonObjectToGuidesList:(NSDictionary*)jsonObj;
-(void)parseCommunitiesArr:(NSArray*)communities;
-(void)removeCommunityWithCommunityID:(NSString*)communityID;
-(CommunityModel*)parseToCommunityModel:(NSArray*)communities;

@end
