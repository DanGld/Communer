//
//  Networking.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AppDelegate.h"
#import "userModel.h"
#import "postModel.h"
#import "ReportModel.h"
#import "QuestionModel.h"
#import "CommentModel.h"
#import "FeedbackModel.h"

@interface Networking : NSObject

@property (nonatomic, strong) AppDelegate* appDelegate;

+ (instancetype)sharedInstance;
-(void)getSMSCodeWithPhoneNumber:(NSString*)phoneNumber;
-(void)getHushTokenWithVarCode:(NSString*)varCode;
- (void)getFirstTimeData;
-(void)fetchNewData;
- (void)getAllCommunitiesWithQuery:(NSString*)query;
- (void)sendJoinRequestAsGuest:(BOOL)asGuest ForCommunityID:(NSString*)communityID;
- (void)sendLeaveRequestForCommunityID:(NSString*)communityID;
- (void)updateUserDetails:(userModel*)user;
- (void)postNewQuestion:(QuestionModel*)question;
- (void)postNewCommunityPost:(postModel*)post;
- (void)postNewQuestionComment:(CommentModel*)answer forPostID:(NSString*)postID;
- (void)postNewCommunityPostComment:(CommentModel*)post ForPostID:(NSString*)postID;
- (void)setAttendingStatus:(NSString*)status ForEventID:(NSString*)eventID;
- (void)getGuidesForCategoryName:(NSString*)catName;
- (void)postNewComment:(CommentModel*)comment ForServiceContactID:(NSString*)serviceContactID;
- (void)sendFeedback:(FeedbackModel*)feedback;
- (void)sendSecurityReport:(ReportModel*)report;
-(void)getCommunityDataWithUrl:(NSString*)communityUrl;


@end
