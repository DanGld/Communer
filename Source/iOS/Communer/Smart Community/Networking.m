//
//  Networking.m
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "Networking.h"
#import "AFNetworking.h"
#import "LocationModel.h"
#import "AppDelegate.h"
#import "DataManagement.h"
#import "LogicServices.h"

//smartcom223.ddns.net is a production server
//smartcom224.ddns.net is a development server

#define BASE_URL @"http://Prod.communer.co/selfow/server/1696164739/"
#define BASE_URL2 @"/com.solidPeak.smartcom.requests.application."

@implementation Networking



+ (instancetype)sharedInstance
{
    static Networking *myInstance = nil;
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
        _appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    }
    return self;
}

-(void)getSMSCodeWithPhoneNumber:(NSString*)phoneNumber {
    phoneNumber = [phoneNumber stringByReplacingOccurrencesOfString:@"+" withString:@""];
    [[NSUserDefaults standardUserDefaults] setObject:phoneNumber forKey:@"phoneNumber"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@mCBO5cFMLBCMVa9QFdIWmDgc%@getLoginData.htm", BASE_URL,BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?phone=%@" , phoneNumber];
    [manager GET:[NSString stringWithFormat:@"%@%@", address, params] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        NSString* status = [NSString stringWithFormat:@"%@" , [responseObject objectForKey:@"status"]];
        NSString* guest = [NSString stringWithFormat:@"%@" , [responseObject objectForKey:@"guest"]];

    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
    }];
}


-(void)getHushTokenWithVarCode:(NSString*)varCode {
    NSString* phoneNumber = [[NSUserDefaults standardUserDefaults] objectForKey:@"phoneNumber"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@mCBO5cFMLBCMVa9QFdIWmDgc%@getUserKeyCheck.htm", BASE_URL,BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?phone=%@&key=%@" , phoneNumber , varCode];
    [manager GET:[NSString stringWithFormat:@"%@%@", address, params] parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        NSString* hashKey = [NSString stringWithFormat:@"%@" , [responseObject objectForKey:@"hash"]];
        if (hashKey && ![hashKey isEqualToString:@""] && ![hashKey isEqualToString:@"keyCode error"]) {
            [[NSUserDefaults standardUserDefaults] setObject:hashKey forKey:@"hashKey"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            [self getFirstTimeData];
        }
        else{
            [[NSNotificationCenter defaultCenter] postNotificationName:@"errorVarCode"
                                                                object:nil
                                                              userInfo:nil];
        }
        
       
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
    }];
}

-(void)getFirstTimeData {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@getFirstTimeData.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?location={\"title\":\"%@\",\"coords\":\"%@\"}", _appDelegate.currentLocation.title, _appDelegate.currentLocation.coords];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        responseObject = [self nestedDictionaryByReplacingNullsWithNil:responseObject];
        
        DataManagement* dataManager = [DataManagement sharedInstance];
        [dataManager parseJsonObjectToModels:responseObject];
        
        NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970]*1000; // set new timeStamp
        [[NSUserDefaults standardUserDefaults] setInteger:timeInterval forKey:@"lastGetDataTimestamp"];
        
        [self fetchNewData];
        
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSLog(@"%@", error);
        
    }];
}

-(void)fetchNewData {    
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@%@%@getData.htm", BASE_URL, _hashKey, BASE_URL2];
    
    NSInteger lastTimestamp = [[NSUserDefaults standardUserDefaults] integerForKey:@"lastGetDataTimestamp"];
    if (!lastTimestamp) {
        lastTimestamp = 0;
    }
    
    NSString* params = [NSString stringWithFormat:@"?location={\"title\":\"%@\",\"coords\":\"%@\"}&timestamp=%li", _appDelegate.currentLocation.title, _appDelegate.currentLocation.coords, (long)lastTimestamp];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        responseObject = [self nestedDictionaryByReplacingNullsWithNil:responseObject];
        
        DataManagement* dataManager = [DataManagement sharedInstance];
        [dataManager parseJsonObjectAndAddToCurrentData:responseObject];
        
        NSString* jsonString = [dataManager.user objectToJson];
        
        [[NSUserDefaults standardUserDefaults] setObject:jsonString forKey:@"jsonData"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970]*1000; // set new timeStamp
        [[NSUserDefaults standardUserDefaults] setInteger:timeInterval forKey:@"lastGetDataTimestamp"];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"%@", error);
    }];
    
}

- (void)getAllCommunitiesWithQuery:(NSString*)query
 {
     NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@%@%@getSrearchResultByQuery.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?querry=%@" , query];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        DataManagement* dataManager = [DataManagement sharedInstance];
        [dataManager parseJsonObjectToCommunitiesResponse:responseObject];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSLog(@"%@", error);
        
    }];
}

- (void)sendJoinRequestAsGuest:(BOOL)asGuest ForCommunityID:(NSString*)communityID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendJoinRequestMod.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?join={\"communityId\":\"%@\",\"asGuest\":%@}" , communityID , asGuest ? @"true" : @"false"];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    manager.responseSerializer = [AFJSONResponseSerializer serializer];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        CommunityModel* community = [[DataManagement sharedInstance] parseToCommunityModel:responseObject];
                [self fetchNewData];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"addCommuitySuccess"
                                                                object:community
                                                              userInfo:nil];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"addCommuityFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
        
    }];
    
}

- (void) sendLeaveRequestForCommunityID:(NSString*)communityID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendLeaveRequest.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?comID=%@" , [[LogicServices sharedInstance] getCurrentCommunity].communityID];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[DataManagement sharedInstance] removeCommunityWithCommunityID:[[LogicServices sharedInstance] getCurrentCommunity].communityID];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"removeCommuitySuccess"
                                                            object:nil
                                                          userInfo:nil];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"updateMenuWithClick"
                                                            object:nil
                                                          userInfo:nil];

    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"removeCommuityFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
        
    }];
}

- (void)updateUserDetails:(userModel*)user {
    
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
NSString* address = [NSString stringWithFormat:@"%@%@%@", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?user={\"userID\":\"%@\",\"name\":\"%@\",\"lastName\":\"%@\",\"gender\":\"%@\",\"mStatus\":\"%@\",\"bDay\":\"%f\",\"email\":\"%@\",\"imageURL\":\"%@\"}" , _hashKey, user.name ? user.name : @"", user.lastname ? user.lastname : @"", user.gender ? user.gender : @"", user.mStatus ? user.mStatus : @"", user.bDayInt ? user.bDayInt : 0, user.email ? user.email : @"", user.userImage? @"" : user.imageUrl? user.imageUrl : @""];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@SendUserUpdateData.htm%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    NSData *imageData;
    if ([user.userImage isKindOfClass:[UIImage class]]) {
         imageData = UIImageJPEGRepresentation(user.userImage, 0.5);
    }
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    
    AFHTTPRequestOperation *op = [manager POST:encodedUrl parameters:nil constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
        if (imageData) {
        [formData appendPartWithFileData:imageData name:@"files" fileName:@"photo.jpg" mimeType:@"image/jpeg"];
        }
    } success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSString* newImageUrl = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        [DataManagement sharedInstance].user.imageUrl = newImageUrl;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"UpdateUserDetailsSuccess"
                                                            object:nil
                                                          userInfo:nil];
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@ ***** %@", operation.responseString, error);
    }];
    [op start];
}

- (void)postNewQuestion:(QuestionModel*)question {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendQuastion.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?quastion={\"title\":\"%@\",\"description\":\"%@\",\"isPublic\":\"%@\",\"communityID\":\"%@\"}" , question.title, question.descr, question.isPublic ? @"true" : @"false", [[LogicServices sharedInstance] getCurrentCommunity].communityID];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionSuccess"
                                                            object:question];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
    }];
}

- (void)postNewQuestionComment:(CommentModel*)answer forPostID:(NSString*)postID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendAnswer.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?answer={\"description\":\"%@\",\"postID\":\"%@\",\"member\":{},\"imageData\":[]}" , answer.content, postID];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionCommentSuccess"
                                                            object:answer];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
       [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionCommentFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
        
    }];
}

- (void)postNewCommunityPostComment:(CommentModel*)post ForPostID:(NSString*)postID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendPostCommentHandler.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?comment={\"description\":\"%@\",\"id\":\"%@\",\"member\":{},\"imageData\":[]}" , post.content, postID];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionCommentSuccess"
                                                            object:post];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostQuestionCommentFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
        
    }];
}

- (void)postNewCommunityPost:(postModel*)post {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendPost.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?post={\"title\":\"%@\",\"imageURL\":\"%@\",\"content\":\"%@\",\"cd\":\"\",\"id\":\"\",\"communityID\":\"%@\",\"member\":{\"name\":\"%@\",\"phone\":\"%@\",\"imageURL\":\"%@\"},\"comments\":[]}" , post.title, post.imageUrl, post.content, [[LogicServices sharedInstance] getCurrentCommunity].communityID, post.communityMember.name, post.communityMember.phone, post.communityMember.imageUrl];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostSuccess"
                                                            object:post];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PostFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
    }];
}

- (void)setAttendingStatus:(NSString*)status ForEventID:(NSString*)eventID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendAttendingStatus.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?event=%@&status=%@" , eventID,status];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"%@", error);
    }];
}

- (void)getGuidesForCategoryName:(NSString*)catName {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@getGuidesBulk.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* myCoords = ((AppDelegate*)[[UIApplication sharedApplication] delegate]).currentLocation.coords;
    NSString* myLocationTitle = ((AppDelegate*)[[UIApplication sharedApplication] delegate]).currentLocation.title;

    NSString* params = [NSString stringWithFormat:@"?category=%@&prefix=%@&startResult=0&numResults=50&rate=%@&price=%@&location={title:%@,coords:%@}", catName, @"", @"", @"", myLocationTitle, myCoords];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address ,params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        NSMutableArray* guidesList = [[DataManagement sharedInstance] parseJsonObjectToGuidesList:responseObject];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"GuidesListSuccess"
                                                            object:guidesList];
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"GuidesListFail"
                                                            object:nil
                                                          userInfo:nil];
        NSLog(@"%@", error);
        
    }];
}
- (void)postNewComment:(CommentModel*)comment ForServiceContactID:(NSString*)serviceContactID {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendGuideRate.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"rating={\"rate\":\"%f\",\"description\":\"%@\",\"workerID\":\"%@\",\"member\":{}}", comment.rate, comment.content, serviceContactID];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
    NSLog(@"%@", error);
        
    }];
}

- (void)sendFeedback:(FeedbackModel*)feedback {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SendSupportMessage.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?feedback={\"type\":\"bug\",\"communityID\":\"%@\",\"topic\":\"%@\",\"title\":\"%@\",\"description\":\"%@\"}",[DataManagement sharedInstance].currentCommunityID, feedback.topic, feedback.title, feedback.descr];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSLog(@"%@", error);
        
    }];
}


- (void)sendSecurityReport:(ReportModel*)report {
    NSString* _hashKey = [[NSUserDefaults standardUserDefaults] objectForKey:@"hashKey"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    NSString* address = [NSString stringWithFormat:@"%@%@%@SecurityReportHandler.htm", BASE_URL, _hashKey, BASE_URL2];
    NSString* params = [NSString stringWithFormat:@"?report={\"location\":{\"title\":\"%@\",\"coords\":\"%@\"},\"type\":\"%@\",\"description\":\"%@\",\"severity\":\"%@\",\"isPoliceReport\":%@,\"isAnnonimus\":%@}", report.location.title, report.location.coords, report.type, report.desc, report.severity, report.isPoliceReport ? @"true" : @"false", report.isAnnonimus ? @"true" : @"false"];
    NSString *encodedUrl = [[NSString stringWithFormat:@"%@%@", address, params]stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [manager GET:encodedUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSLog(@"%@", error);
        
    }];
}

-(void)getCommunityDataWithUrl:(NSString*)communityUrl {
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [[manager requestSerializer] setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
    [manager GET:communityUrl parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"getCommunityJsonDataResponse"
                                                            object:responseObject];

    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSLog(@"%@", error);
        
    }];
}


- (NSDictionary *) dictionaryByReplacingNullsWithNil:(NSDictionary*)sourceDictionary {
    
    NSMutableDictionary *replaced = [NSMutableDictionary dictionaryWithDictionary:sourceDictionary];
    const id nul = [NSNull null];
    
    for(NSString *key in replaced) {
        const id object = [sourceDictionary objectForKey:key];
        if(object == nul) {
            [replaced setValue:nil forKey:key];
        }
    }
    return [NSDictionary dictionaryWithDictionary:replaced];
}

-(NSDictionary *) nestedDictionaryByReplacingNullsWithNil:(NSDictionary*)sourceDictionary
{
    NSMutableDictionary *replaced = [NSMutableDictionary dictionaryWithDictionary:sourceDictionary];
    const id nul = [NSNull null];
    const NSString *blank = @"";
    [sourceDictionary enumerateKeysAndObjectsUsingBlock:^(id key, id object, BOOL *stop) {
        object = [sourceDictionary objectForKey:key];
        if([object isKindOfClass:[NSDictionary class]])
        {
            NSDictionary *innerDict = object;
            [replaced setObject:[self nestedDictionaryByReplacingNullsWithNil:innerDict] forKey:key];
            
        }
        else if([object isKindOfClass:[NSArray class]]){
            NSMutableArray *nullFreeRecords = [NSMutableArray array];
        for (id record in object) {
                
                if([record isKindOfClass:[NSDictionary class]])
                {
                    NSDictionary *nullFreeRecord = [self nestedDictionaryByReplacingNullsWithNil:record];
                    [nullFreeRecords addObject:nullFreeRecord];
                }
            }
            if (![key isEqualToString:@"widgets"] && ![key isEqualToString:@"visibleModules"] && ![key isEqualToString:@"additions"] && ![key isEqualToString:@"communitiesUrl"]) { // fix for widgets and visible modules and additions
                [replaced setObject:nullFreeRecords forKey:key];
            }

        }
        else
        {
            if(object == nul) {
                [replaced setObject:blank forKey:key];
            }
        }
    }];
    
    return [NSDictionary dictionaryWithDictionary:replaced];
}




@end
