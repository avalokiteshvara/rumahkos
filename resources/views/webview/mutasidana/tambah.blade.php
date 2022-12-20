@extends('webview.layout')
@section('content')
	<!-- Content Header (Page header) -->
    
    <section class="content" style="padding-top: 10px;">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <!-- general form elements -->
            <div class="card card-primary">
              <div class="card-header">
                <h3 class="card-title">Form Permohonan Penarikan Dana</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'webview/permohonan-penarikan-dana/' . $user_id,'files' => true]) }}
                <div class="card-body">
                  
                  <div class="form-group">
                    {{ Form::label('bank_nama', 'Bank') }}  
                    {{ Form::text('bank_nama',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_nama'))
                          <div class="text-danger">
                              {{ $errors->first('bank_nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('bank_rekening', 'Nomor rekening') }}  
                    {{ Form::text('bank_rekening',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_rekening'))
                          <div class="text-danger">
                              {{ $errors->first('bank_rekening')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('bank_penerima', 'Nama Pemilik Rekening') }}  
                    {{ Form::text('bank_penerima',null,['class' => 'form-control']) }}
                    @if($errors->has('bank_penerima'))
                          <div class="text-danger">
                              {{ $errors->first('bank_penerima')}}
                          </div>
                     @endif
                  </div>


                  <div class="form-group">
                    {{ Form::label('nominal', 'Nominal') }}  
                    {{ Form::number('nominal', null ,['class' => 'form-control']) }}
                    @if($errors->has('nominal'))
                          <div class="text-danger">
                              {{ $errors->first('nominal')}}
                          </div>
                     @endif
                  </div>

                </div>

                <div class="card-footer">                  
                  {{ Form::submit('Submit',['class' => 'btn btn-primary']) }}
                </div>
              {{ Form::close() }}  

              
            </div>
            <!-- /.card -->

          </div>
        </div>
      </div>
    </section>
    
@endsection
