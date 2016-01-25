//
//  EventOrMessageModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "KVCBaseObject.h"


@interface EventOrMessageModel : KVCBaseObject

@property (nonatomic, strong) NSString* eventID;
@property (nonatomic, strong) NSString* name;
@property (nonatomic,) NSTimeInterval sTime;
@property (nonatomic) NSTimeInterval eTime;
@property (nonatomic) NSTimeInterval date;
@property (nonatomic, strong) LocationModel* locationObj;
@property (nonatomic, strong) NSMutableArray* participants;
@property (nonatomic, strong) NSString* content;
@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic, strong) NSString* readStatus;
@property (nonatomic, strong) NSString* type;
@property (nonatomic) BOOL isFree;
@property (nonatomic, strong) NSString* buyLink;
@property (nonatomic, strong) NSString* attendingStatus;
@property (nonatomic, strong) NSMutableArray* comments;
@property (nonatomic) BOOL isActive;

@end
