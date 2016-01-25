//
//  LogicServices.h
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PrayerDetailsModel.h"
#import "DataManagement.h"

@interface LogicServices : NSObject

@property (strong, nonatomic) DataManagement *dataManager;

+ (instancetype)sharedInstance;

- (NSArray*)getAllCommunitiesActivities;
- (NSArray*)getAllCommunityFacilities;
- (NSArray*)getAllCommunityEvents;
- (dailyEventsModel*)getCommunityDailyEventsForDate:(NSDate*)date;
- (PrayerDetailsModel*)getAllPrayersForDate:(NSDate*)date;
- (NSArray*)getNextPrayersForDate:(NSDate*)date;
- (NSArray*)getLastTwoMessages;
- (NSArray*)getAllPersonalMessages;
- (NSArray*)getAllGeneralMessages;
- (NSArray*)getLast10Images;
- (NSArray*)getAllImagesEvents;
- (NSArray*)getAllGuidesCategories;
-(NSArray*)getAllCommunityPublicQuestions;
-(NSArray*)getAllCommunityPrivateQuestions;
-(postModel*)getLastCommunityPost;
-(postModel*)getLastPublicQuestion;
-(NSArray*)getSecurityReportArray;
-(CommunityModel*)getCurrentCommunity;

-(void)addCommunityPrivateQuestion:(postModel*)question;
-(void)addCommunityPublicQuestion:(postModel*)question;
-(void)addCommunityPrivateQuestionComment:(CommentModel*)comment ForQuestionID:(NSString*)questionID;
-(void)addCommunityPublicQuestionComment:(CommentModel*)comment ForQuestionID:(NSString*)questionID;
-(void)addCommunityPost:(postModel*)post;
-(void)addCommunityPostComment:(CommentModel*)comment ForPostID:(NSString*)postID;
-(void)addNewMessage:(EventOrMessageModel*)message;
-(void)addNewEvents:(NSMutableArray*)events;


@end
