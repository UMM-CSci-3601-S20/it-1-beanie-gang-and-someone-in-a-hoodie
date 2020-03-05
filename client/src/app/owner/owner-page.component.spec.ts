import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRouteStub } from '../../testing/activated-route-stub';

// Owner imports
import { Owner } from './owner';
import { OwnerService } from './owner.service';
import { OwnerPageComponent } from './owner-page.component';
import { MockOwnerService } from '../../testing/owner.service.mock';
