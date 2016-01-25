//
//  userModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "CommunityModel.h"
#import "KVCBaseObject.h"

@interface userModel : KVCBaseObject

@property (nonatomic, strong) NSString* userID;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* lastname;
@property (nonatomic, strong) NSString* gender;
@property (nonatomic, strong) NSString* mStatus;
@property (nonatomic) NSDate* bDay;
@property (nonatomic) NSTimeInterval bDayInt;
@property (nonatomic, strong) NSString* userBDay;
@property (nonatomic, strong) NSString* userBMonth;
@property (nonatomic, strong) NSString* userBYear;
@property (nonatomic, strong) NSString* email;
@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic, strong) NSMutableArray* communities;
@property (nonatomic) BOOL isRabai;
@property (nonatomic) UIImage* userImage;

@end
