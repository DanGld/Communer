//
//  QuestionModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface QuestionModel : KVCBaseObject

@property (nonatomic, strong) NSString* questionID;
@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* descr;
@property (nonatomic) BOOL isPublic  ;

@end
