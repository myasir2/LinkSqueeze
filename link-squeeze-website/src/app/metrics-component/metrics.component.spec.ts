import {ComponentFixture, TestBed} from "@angular/core/testing"

import {MetricsComponent} from "./metrics.component"

describe("MetricsComponentComponent", () => {
    let component: MetricsComponent
    let fixture: ComponentFixture<MetricsComponent>

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [MetricsComponent],
        })
            .compileComponents()

        fixture = TestBed.createComponent(MetricsComponent)
        component = fixture.componentInstance
        fixture.detectChanges()
    })

    it("should create", () => {
        expect(component).toBeTruthy()
    })
})
