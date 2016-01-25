//
//  ServiceContactModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "KVCBaseObject.h"

@interface ServiceContactModel : KVCBaseObject

@property (nonatomic, strong) NSString* serviceContactID;
@property (nonatomic, strong) NSString* imageUrl;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) LocationModel* location;
@property (nonatomic, strong) NSString* phoneNumber;
@property (nonatomic) double rating;
@property (nonatomic, strong) NSMutableArray* comments;

@end
