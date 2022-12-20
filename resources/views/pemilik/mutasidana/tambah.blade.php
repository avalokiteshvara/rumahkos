@extends('pemilik.layout')
@section('content')
	<!-- Content Header (Page header) -->
    <section class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1>Permohonan Penarikan Dana</h1>
          </div>
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik') }}">Beranda</a></li>
              <li class="breadcrumb-item"><a href="{{ URL::to('/pemilik/penarikan-dana') }}">Data Penarikan Dana</a></li>
              <li class="breadcrumb-item active">Permohonan Baru</li>
            </ol>
          </div>
        </div>
      </div><!-- /.container-fluid -->
    </section>
    <section class="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <!-- general form elements -->
            <div class="card card-primary">
              <div class="card-header">
                <h3 class="card-title">Form Permohonan</h3>
              </div>
              <!-- /.card-header -->
              <!-- form start -->
             
             {{ Form::open(['url' => 'pemilik/permohonan-penarikan-dana','files' => true]) }}
                <div class="card-body">
                  
                  <div class="form-group">
                    {{ Form::label('bank_nama', 'Bank') }}  
                    {{ Form::text('bank_nama',$rek_info[0],['class' => 'form-control']) }}
                    @if($errors->has('bank_nama'))
                          <div class="text-danger">
                              {{ $errors->first('bank_nama')}}
                          </div>
                     @endif
                  </div>
                
                  <div class="form-group">
                    {{ Form::label('bank_rekening', 'Nomor rekening') }}  
                    {{ Form::text('bank_rekening',$rek_info[1],['class' => 'form-control']) }}
                    @if($errors->has('bank_rekening'))
                          <div class="text-danger">
                              {{ $errors->first('bank_rekening')}}
                          </div>
                     @endif
                  </div>

                  <div class="form-group">
                    {{ Form::label('bank_penerima', 'Nama Pemilik Rekening') }}  
                    {{ Form::text('bank_penerima',$rek_info[2],['class' => 'form-control']) }}
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
